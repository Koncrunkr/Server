package ru.comgrid.server.api.user;

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

/**
 * User service, that has most commonly used user targeted endpoints.
 * (frontend must not specify any credentials since
 *  it is done automatically if person is authorized)
 * @author MediaNik
 */
@RestController
@RequestMapping(value = "/user", produces = "application/json")
public class UserService{

    private final PersonRepository personRepository;
    private final ChatParticipantsRepository chatParticipantsRepository;
    private final ChatRepository chatRepository;
    private final int defaultPageSize;

    /**
     * @hidden
     */
    public UserService(
        PersonRepository personRepository,
        ChatParticipantsRepository chatParticipantsRepository,
        ChatRepository chatRepository,
        @Value("${ru.comgrid.chat.default-page-size}") int defaultPageSize
    ){
        this.personRepository = personRepository;
        this.chatParticipantsRepository = chatParticipantsRepository;
        this.chatRepository = chatRepository;
        this.defaultPageSize = defaultPageSize;
    }

    /**
     * Get this {@link Person}.
     * <p>
     * Add "includeChats=true" param if you want to include chats in returned {@link Person} object
     * </p>
     *
     * <pre>
     * Example:
     *
       fetch(
           "https://comgrid.ru:8443/user/info?includeChats=true",
           {
               method: "GET",
               credentials: "include", // compulsory
               headers: {"Content-Type": "application/json"}
           }
       ).then(
           response => response.text()
       ).then(
           html => console.log(html)
       )
     * </pre>
     * @param user Authenticated user from Spring security
     * @param includeChats boolean value whether to include {@link Chat} list or not
     * @return {@link Person} in json format
     */
    @GetMapping("/info")
    public ResponseEntity<String> getUserInfo(
        @AuthenticationPrincipal OAuth2User user,
        @RequestParam(required = false, defaultValue = "false") boolean includeChats
    ){
        var id = UserHelp.extractId(user);
        @SuppressWarnings("OptionalGetWithoutIsPresent") // We know because authentication took place
        Person person = personRepository.findById(id).get();

        if(includeChats){
            List<Long> chatIds = chatParticipantsRepository.findAllChatsByPerson(
                person.getId(),
                Pageable.ofSize(defaultPageSize)
            ).getContent();
            Iterable<Chat> chats = chatRepository.findAllById(chatIds);
            person.setChats(chats);
        }

        return ResponseEntity.ok(person.toString());
    }

    @GetMapping("/login")
    public ResponseEntity<String> getUserInfo(){
        return SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().isAuthenticated() ?
            ResponseEntity.ok().build() : ResponseEntity.status(401).build();
    }

//
//    @GetMapping("/chats")
//    public ResponseEntity<String> getUserChats(){
//
//    }
}
