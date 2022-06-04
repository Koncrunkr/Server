package ru.comgrid.server.service.table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.comgrid.server.controller.table.AddParticipantRequest;
import ru.comgrid.server.controller.table.ChangeRightsRequest;
import ru.comgrid.server.controller.table.NewChat;
import ru.comgrid.server.controller.util.ImageEntity;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.exception.NotFoundException;
import ru.comgrid.server.exception.OutOfBoundsRequestException;
import ru.comgrid.server.exception.RequestException;
import ru.comgrid.server.model.Chat;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.model.TableParticipants;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.repository.PersonRepository;
import ru.comgrid.server.security.AppProperties;
import ru.comgrid.server.service.AccessService;
import ru.comgrid.server.service.file.FileService;
import ru.comgrid.server.util.EnumSet0;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TableService{

    private final ChatRepository chatRepository;
    private final ChatParticipantsRepository participantsRepository;
    private final PersonRepository personRepository;
    private final AccessService accessService;
    private final int maxTableSize;
    private final FileService fileService;

    public TableService(
        @Autowired ChatRepository chatRepository,
        @Autowired ChatParticipantsRepository participantsRepository,
        @Autowired PersonRepository personRepository,
        @Autowired AccessService accessService,
        @Autowired AppProperties appProperties,
        @Autowired FileService fileService
    ){
        this.chatRepository = chatRepository;
        this.participantsRepository = participantsRepository;
        this.personRepository = personRepository;
        this.accessService = accessService;
        maxTableSize = appProperties.getTable().getMaxTableSize();
        this.fileService = fileService;
    }

    @NotNull
    @Transactional
    public Chat createChat(NewChat newChat, BigDecimal userId){
        ImageEntity imageEntity = fileService.uploadImage(newChat.getAvatarFile(), newChat.getAvatarLink());
        checkBorders(newChat.getWidth(), newChat.getHeight());
        Chat chat = new Chat(userId, newChat.getName(), newChat.getWidth(), newChat.getHeight(), imageEntity.getUrl());
        chat.setCreated(LocalDateTime.now(Clock.systemUTC()));
        chat = chatRepository.save(chat);
        participantsRepository.save(new TableParticipants(chat.getId(), userId, EnumSet0.allOf(Right.class), LocalDateTime.now(Clock.systemUTC())));
        return chat;
    }

    private void checkBorders(int width, int height){
        if(width <= 0 || height <= 0)
            throw new OutOfBoundsRequestException("negative_size");
        if(width*height > maxTableSize)
            throw new OutOfBoundsRequestException("too_large");
    }

    @NotNull
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Chat getChat(long chatId, boolean includeParticipants, BigDecimal userId){
        if(!participantsRepository.existsByChatAndPerson(chatId, userId))
            throw new NotFoundException("chat.not_found");

        @SuppressWarnings("OptionalGetWithoutIsPresent") // We know it, because it is in participantsRepository
        Chat chat = chatRepository.findById(chatId).get();

        if(includeParticipants){
            List<BigDecimal> personIds = participantsRepository.findAllByChat(
                chatId
            );
            Iterable<Person> participants = personRepository.findAllById(personIds);
            chat.setParticipants(participants);
        }

        return chat;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addParticipantToChat(AddParticipantRequest addParticipantRequest, BigDecimal adminUserId){
        var newUserId = new BigDecimal(addParticipantRequest.getUserId());

        if(!accessService.hasAccessTo(adminUserId, addParticipantRequest.getChatId(), Right.AddUsers)){
            throw new IllegalAccessException("add_participant");
        }

        if(!personRepository.existsById(newUserId)){
            throw new NotFoundException("user.not_found");
        }

        if(participantsRepository.existsByChatAndPerson(addParticipantRequest.getChatId(), newUserId)){
            throw new RequestException(422, "user.already_participant");
        }

        participantsRepository.save(new TableParticipants(
            addParticipantRequest.getChatId(),
            newUserId,
            EnumSet0.of(Right.Read, Right.SendMessages, Right.EditOwnMessages, Right.CreateCellUnions, Right.EditOwnCellUnions),
            LocalDateTime.now()
        ));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void changeRightsOfUser(ChangeRightsRequest changeRightsRequest, BigDecimal adminUserId, BigDecimal existingUserId){
        if(!accessService.hasAccessTo(adminUserId, changeRightsRequest.getChatId(), Right.ManageUsers)){
            throw new IllegalAccessException("manage_users");
        }

        if(!personRepository.existsById(existingUserId)){
            throw new RequestException(404, "user.not_found");
        }

        TableParticipants participant = participantsRepository.findByChatAndPerson(changeRightsRequest.getChatId(), existingUserId);

        if(participant == null){
            throw new RequestException(404, "user.not_in_chat");
        }

        participant.setRights(changeRightsRequest.getRights());

        participantsRepository.save(participant);
    }
}
