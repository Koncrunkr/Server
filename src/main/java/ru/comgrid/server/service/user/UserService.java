package ru.comgrid.server.service.user;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.exception.NotFoundException;
import ru.comgrid.server.exception.RequestException;
import ru.comgrid.server.model.Chat;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.repository.PersonRepository;
import ru.comgrid.server.security.AppProperties;
import ru.comgrid.server.security.user.info.UserPrincipal;
import ru.comgrid.server.util.ColorUtil;

import java.math.BigDecimal;
import java.util.List;

import static ru.comgrid.server.security.UserRole.ROLE_ADMIN;

@Service
public class UserService{
    private static final SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority(ROLE_ADMIN);
    private final PersonRepository personRepository;
    private final ChatParticipantsRepository participantsRepository;
    private final ChatRepository chatRepository;
    private final String existingAdminKey;

    public UserService(
        @Autowired PersonRepository personRepository,
        @Autowired ChatParticipantsRepository participantsRepository,
        @Autowired ChatRepository chatRepository,
        @Autowired AppProperties appProperties
    ){
        this.personRepository = personRepository;
        this.participantsRepository = participantsRepository;
        this.chatRepository = chatRepository;
        existingAdminKey = appProperties.getAuth().getAdminKey();
    }

    @NotNull
    public Person getPersonById(String userId, boolean includeChats, BigDecimal id){
        Person person = personRepository.findById(id).orElseThrow(
            () -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "user.not_found")
        );

        if(includeChats && userId == null){
            List<Long> chatIds = participantsRepository.findAllChatsByPerson(
                person.getId()
            );
            List<Chat> chats = chatRepository.findAllById(chatIds);
            person.setChats(chats);
        }

        if(person.getColor() == null){
            person.setColor(ColorUtil.randomColor());
            personRepository.save(person);
        }

        return person;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void makePersonAnAdmin(UserPrincipal user, String adminKey, String personId){
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

    private void checkAdminKey(String adminKey){
        if(!existingAdminKey.equals(adminKey))
            throw new IllegalAccessException("admin_key.wrong");
    }

    public void changeUsername(String username, BigDecimal userId){
        if(username.startsWith("@"))
            username = username.substring(1);
        if(username.length() > 24)
            throw new RequestException(400, "username.too_long");
        @SuppressWarnings("OptionalGetWithoutIsPresent") // we know, because user is authenticated
        var person = personRepository.findById(userId).get();
        person.setUsername(username);
        personRepository.save(person);
    }

    public List<Person> getUsersByUsername(String username){
        if(username.startsWith("@"))
            username = username.substring(1);
        if(username.length() > 24)
            throw new RequestException(400, "username.too_long");

        return personRepository.findPeople(username);
    }
}
