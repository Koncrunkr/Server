package ru.comgrid.server.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.exception.NotFoundException;
import ru.comgrid.server.exception.RequestException;
import ru.comgrid.server.model.Chat;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.repository.PersonRepository;
import ru.comgrid.server.security.AppProperties;
import ru.comgrid.server.security.annotation.CurrentUser;
import ru.comgrid.server.security.user.info.UserPrincipal;

import java.math.BigDecimal;
import java.util.List;

import static ru.comgrid.server.security.UserRole.ROLE_ADMIN;

@RestController
@RequestMapping(value = "/user", produces = "application/json; charset=utf-8")
@SecurityRequirement(name = "bearerAuth")
public class UserController{

    private final AppProperties appProperties;
    private final PersonRepository personRepository;
    private final ChatParticipantsRepository chatParticipantsRepository;
    private final ChatRepository chatRepository;

    /**
     * @hidden
     */
    public UserController(
        AppProperties appProperties,
        PersonRepository personRepository,
        ChatParticipantsRepository chatParticipantsRepository,
        ChatRepository chatRepository
    ){
        this.appProperties = appProperties;
        this.personRepository = personRepository;
        this.chatParticipantsRepository = chatParticipantsRepository;
        this.chatRepository = chatRepository;
    }


    @Operation(summary = "Get user info")
    @GetMapping("/info")
    public Person getUserInfo(
        @CurrentUser UserPrincipal user,
        @RequestParam(required = false) String userId,
        @RequestParam(required = false, defaultValue = "false") boolean includeChats
    ){
        BigDecimal id;
        if(userId == null){
            id = UserHelp.extractId(user);
        }else{
            id = UserHelp.extractId(userId);
        }
        @SuppressWarnings("OptionalGetWithoutIsPresent") // We know because authentication took place
        Person person = personRepository.findById(id).get();

        if(includeChats && userId == null){
            List<Long> chatIds = chatParticipantsRepository.findAllChatsByPerson(
                person.getId()
            );
            List<Chat> chats = chatRepository.findAllById(chatIds);
            person.setChats(chats);
        }

        return person;
    }

    private static final SimpleGrantedAuthority anonymous = new SimpleGrantedAuthority("ROLE_ANONYMOUS");

    @Operation(summary = "Check if user is logged in")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User is logged in", content = @Content()),
        @ApiResponse(responseCode = "401", description = "User is not logged in", content = @Content()),
    })
    @GetMapping("/login")
    public ResponseEntity<String> checkIfUserLoggedIn(
        @CurrentUser UserPrincipal user
    ){
        return user != null ? ResponseEntity.ok().build() : ResponseEntity.status(401).build();
    }

    private static final SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority(ROLE_ADMIN);
    @PostMapping("/make_admin")
    @Transactional
    public void makePersonAnAdmin(
        @CurrentUser UserPrincipal user,
        @RequestParam(required = false) String adminKey,
        @RequestParam(required = false) String personId
    ){
        if(adminKey == null){
            if(!user.containsAuthority(adminAuthority)){
                throw new IllegalAccessException("user.not_admin");
            }
        }else{
            checkAdminKey(adminKey);
        }

        if(personId == null){
            if(!user.containsAuthority(adminAuthority)){
                @SuppressWarnings("OptionalGetWithoutIsPresent")
                var person = personRepository.findById(user.getId()).get();
                person.getAuthorities().add(adminAuthority);
                personRepository.save(person);
            }
        }else{
            var person = personRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("user.not_found"));
            person.getAuthorities().add(adminAuthority);
            personRepository.save(person);
        }
    }

    @PostMapping("/username")
    @Transactional
    public void changeUsername(
        @CurrentUser UserPrincipal user,
        @RequestParam String username
    ){
        if(username.startsWith("@"))
            username = username.substring(1);
        if(username.length() > 24)
            throw new RequestException(400, "username.too_long");
        var person = personRepository.findById(user.getId()).get();
        person.setUsername(username);
        personRepository.save(person);
    }

    private void checkAdminKey(String adminKey){
        if(!appProperties.getAuth().getAdminKey().equals(adminKey))
            throw new IllegalAccessException("admin_key.wrong");
    }
}
