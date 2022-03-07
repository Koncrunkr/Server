package ru.comgrid.server.api.user;

import org.aspectj.bridge.IMessageContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.comgrid.server.api.table.TableHelp;
import ru.comgrid.server.exception.*;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.model.*;
import ru.comgrid.server.repository.CellUnionRepository;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.repository.MessageRepository;
import ru.comgrid.server.util.EnumSet0;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.comgrid.server.api.message.MessageHelp.userDestination;
import static ru.comgrid.server.api.user.UserHelp.samePerson;

@Service
public class AccessService{

    private final ChatParticipantsRepository participantsRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final CellUnionRepository cellUnionRepository;
    private final ChatRepository chatRepository;

    public AccessService(
        @Autowired ChatParticipantsRepository participantsRepository,
        @Autowired SimpMessagingTemplate messagingTemplate,
        @Autowired MessageRepository messageRepository,
        @Autowired CellUnionRepository cellUnionRepository,
        @Autowired ChatRepository chatRepository
    ){
        this.participantsRepository = participantsRepository;
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
        this.cellUnionRepository = cellUnionRepository;
        this.chatRepository = chatRepository;
    }

    public boolean hasAccessTo(Person person, long chatId, Right right){
        return hasAccessTo(person.getId(), chatId, right);
    }

    public boolean hasAccessTo(BigDecimal userId, long chatId, Right right){
        Optional<TableParticipants> participance = participantsRepository.findById(new TableParticipant(
            chatId,
            userId
        ));
        if(participance.isEmpty())
            return false;

        return participance.get().rights().contains(right);
    }

    public boolean hasAccessToSendMessage(BigDecimal personId, Message message){
        if(message.getId() != null){
            sendException(personId, new EditIsNotAllowedException());
            return false;
        }
        if(!hasAccessTo(personId, message.getChatId(), Right.SendMessages)){
            sendException(personId, new IllegalAccessException("chat.send_message"));
            return false;
        }
        Chat chat = chatRepository.findById(message.getChatId()).get();
        if(!checkBorders(chat, message)){
            sendException(personId, new OutOfBoundsRequestException("message." + message.getChatId() + "." + message.getX() + "." + message.getY()));
            return false;
        }
        Optional<Message> existingMessage = messageRepository.findMessageByChatIdAndXAndY(message.getChatId(), message.getX(), message.getY());
        if(existingMessage.isPresent()){
            sendException(personId, new MessageAlreadyExistsException());
            return false;
        }

        return true;
    }

    private boolean checkBorders(Chat chat, Message message){
        return message.getX() < chat.getWidth() && message.getX() >= 0 &&
            message.getY() < chat.getHeight() && message.getY() >= 0;
    }

    private void sendException(BigDecimal personId, WrongRequestException wrongRequestException){
        messagingTemplate.convertAndSend(userDestination(personId), wrongRequestException);
    }

    public boolean hasAccessToEditMessage(BigDecimal personId, Message message){
        if(message.getId() == null){
            sendException(personId, new SendIsNotAllowedException());
            return false;
        }

        Long chatId = message.getChatId();
        Optional<Message> existingMessage = messageRepository.findMessageByChatIdAndXAndY(chatId, message.getX(), message.getY());
        if(existingMessage.isEmpty()){
            sendException(personId, new MessageNotFoundException());
            return false;
        }

        if(existingMessage.get().isSender(personId)){
            return hasAccessTo(personId, chatId, Right.EditOwnMessages, "message.edit");
        }

        return hasAccessTo(personId, chatId, Right.EditOthersMessages, "message.override");
    }

    private boolean hasAccessTo(BigDecimal personId, Long chatId, Right right, String errorCode){
        if(hasAccessTo(personId, chatId, right)){
            return true;
        }else{
            sendException(personId, new IllegalAccessException(errorCode));
            return false;
        }
    }

    public boolean hasAccessToCreateCellUnion(BigDecimal personId, CellUnion newCellUnion){
        if(newCellUnion.getId() != null){
            sendException(personId, new EditIsNotAllowedException());
            return false;
        }
        if(hasAccessTo(personId, newCellUnion.getChatId(), Right.SendMessages, "message.send"))
            return doesNotIntersect(personId, newCellUnion, null, true);
        else
            return false;
    }

    public boolean hasAccessToEditCellUnion(BigDecimal personId, CellUnion cellUnion){
        if(cellUnion.getId() == null){
            sendException(personId, new SendIsNotAllowedException());
            return false;
        }
        Optional<TableParticipants> participance = participantsRepository.findById(new TableParticipant(
            cellUnion.getChatId(),
            personId
        ));

        if(participance.isEmpty())
            return false;
        var rights = participance.get().rights();

        Optional<CellUnion> existingCellUnion = cellUnionRepository.findById(cellUnion.getId());
        if(existingCellUnion.isEmpty()){
            sendException(personId, new MessageNotFoundException());
            return false;
        }

        return hasAccessToEditCellUnion(personId, cellUnion, existingCellUnion.get(), rights);
    }

    private boolean hasAccessToEditCellUnion(BigDecimal personId, CellUnion newCellUnion, CellUnion existingCellUnion, EnumSet0<Right> rights){
        if(rights.contains(Right.EditOthersCellUnions))
            return true; // user can edit all cell unions
        if(!rights.contains(Right.EditOwnCellUnions))
            return false; // user does not have right to edit any cell unions

        if(samePerson(existingCellUnion.getCreatorId(), personId)){
            if(doesNotIntersect(personId, newCellUnion, existingCellUnion.getId(), false)){
                return true;
            }
            sendException(personId, new CellUnionAlreadyExistsException());
            return false;
        }
        // cell union is not person's. Also, person can't modify other's cell unions
        sendException(personId, new IllegalAccessException("cell_union.edit"));
        return false;
    }

    private boolean doesNotIntersect(BigDecimal personId, CellUnion cellUnion, Long existingCellUnionId, boolean sameSenderNotAllowed){
        if(sameSenderNotAllowed){
            return !cellUnionRepository.existsCellUnion(
                cellUnion.getChatId(),
                cellUnion.getXcoordLeftTop(),
                cellUnion.getYcoordLeftTop(),
                cellUnion.getXcoordRightBottom(),
                cellUnion.getYcoordRightBottom()
            );
        }

        List<CellUnion> cellUnionsIntersected = cellUnionRepository.findAllByChat(
            cellUnion.getChatId(),
            cellUnion.getXcoordLeftTop(),
            cellUnion.getYcoordLeftTop(),
            cellUnion.getXcoordRightBottom(),
            cellUnion.getYcoordRightBottom()
        );

        List<CellUnion> inside = new ArrayList<>();

        for(var intersected : cellUnionsIntersected){
            if(!samePerson(intersected.getCreatorId(), personId)){
                return false;
            }
            if(TableHelp.isInside(intersected, cellUnion) && !cellUnion.getId().equals(existingCellUnionId)){
                inside.add(intersected);
            }
        }
        cellUnionRepository.deleteAll(inside);
        return true;
    }
}
