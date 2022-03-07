package ru.comgrid.server.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.model.Chat;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.repository.PersonRepository;
import ru.comgrid.server.repository.ChatRepository;

import java.util.List;


@RestController
@RequestMapping(value = "/user", produces = "application/json; charset=utf-8")
public class UserController{

    private final PersonRepository personRepository;
    private final ChatParticipantsRepository chatParticipantsRepository;
    private final ChatRepository chatRepository;

    /**
     * @hidden
     */
    public UserController(
        PersonRepository personRepository,
        ChatParticipantsRepository chatParticipantsRepository,
        ChatRepository chatRepository
    ){
        this.personRepository = personRepository;
        this.chatParticipantsRepository = chatParticipantsRepository;
        this.chatRepository = chatRepository;
    }


    @Operation(summary = "Get user info")
    @GetMapping("/info")
    public ResponseEntity<Person> getUserInfo(
        @AuthenticationPrincipal OAuth2User user,
        @RequestParam(required = false, defaultValue = "false") boolean includeChats
    ){
        var id = UserHelp.extractId(user);
        @SuppressWarnings("OptionalGetWithoutIsPresent") // We know because authentication took place
        Person person = personRepository.findById(id).get();

        if(includeChats){
            List<Long> chatIds = chatParticipantsRepository.findAllChatsByPerson(
                person.getId()
            );
            Iterable<Chat> chats = chatRepository.findAllById(chatIds);
            person.setChats(chats);
        }

        return ResponseEntity.ok(person);
    }

    @Operation(summary = "Check if user is logged in")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User is logged in", content = @Content()),
        @ApiResponse(responseCode = "401", description = "User is not logged in", content = @Content()),
    })
    @GetMapping("/login")
    public ResponseEntity<String> getUserInfo(){
        return SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().isAuthenticated() ?
            ResponseEntity.ok().build() : ResponseEntity.status(401).build();
    }

}
