package ru.comgrid.server.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.comgrid.server.controller.WebsocketDestination;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.exception.*;
import ru.comgrid.server.model.*;
import ru.comgrid.server.repository.CellUnionRepository;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.repository.MessageRepository;
import ru.comgrid.server.service.table.TableHelp;
import ru.comgrid.server.util.EnumSet0;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ru.comgrid.server.service.user.UserHelp.samePerson;

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

    public boolean hasAccessTo(BigDecimal personId, long chatId, Right... rights){
        Optional<TableParticipants> participance = participantsRepository.findById(new TableParticipant(
            chatId,
            personId
        ));
        if(participance.isEmpty()){
            sendException(personId, new IllegalAccessException("person.not_in_chat"));
            return false;
        }

        return participance.get().rights().containsAll(Arrays.asList(rights));
    }

    public boolean hasAccessToSendMessage(BigDecimal personId, Message message){
//        if(message.getId() != null){
//            sendException(personId, new EditIsNotAllowedException());
//            return false;
//        }
        if(!hasAccessTo(personId, message.getChatId(), Right.SendMessages)){
            sendException(personId, new IllegalAccessException("chat.send_message"));
            return false;
        }

        if(checkForNullability(personId, message)) return false;

        Chat chat = chatRepository.findById(message.getChatId()).get();
        if(!checkBorders(chat, message)){
            sendException(personId, new OutOfBoundsRequestException("message.out_of_bounds" + message.getChatId() + "." + message.getX() + "." + message.getY()));
            return false;
        }
        Optional<Message> existingMessage = messageRepository.findMessageByChatIdAndXAndY(message.getChatId(), message.getX(), message.getY());
        if(existingMessage.isPresent()){
            if(
                existingMessage.get().isSender(personId) &&
                !hasAccessTo(personId, message.getChatId(), Right.EditOwnMessages)
            ||
                !existingMessage.get().isSender(personId) &&
                !hasAccessTo(personId, message.getChatId(), Right.EditOthersMessages)
            ){
                sendException(personId, new MessageAlreadyExistsException());
                return false;
            }
        }

        return true;
    }

    private boolean checkForNullability(BigDecimal personId, Message message){
        if(message.getChatId() == null){
            sendException(personId, new RequestException(400, "chat.null"));
            return true;
        }

        if(message.getX() == null || message.getY() == null){
            sendException(personId, new RequestException(400, "coordinates.null"));
            return true;
        }

        if(!StringUtils.hasText(message.getText())){
            sendException(personId, new RequestException(400, "text.null"));
            return true;
        }
        return false;
    }

    private boolean checkBorders(Chat chat, Message message){
        return message.getX() < chat.getWidth() && message.getX() >= 0 &&
            message.getY() < chat.getHeight() && message.getY() >= 0;
    }

    private void sendException(BigDecimal personId, RequestException requestException){
        messagingTemplate.convertAndSend(WebsocketDestination.USER.destination(personId, personId), requestException);
    }

    public boolean hasAccessToEditMessage(BigDecimal personId, Message message){
//        if(message.getId() == null){
//            sendException(personId, new SendIsNotAllowedException());
//            return false;
//        }

        if(checkForNullability(personId, message)) return false;

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

        if(checkForNullability(personId, newCellUnion)) return false;

        return hasAccessTo(personId, newCellUnion.getChatId(), Right.SendMessages, "message.send") &&
            doesNotIntersect(personId, newCellUnion, null);
    }

    private boolean checkForNullability(BigDecimal personId, CellUnion newCellUnion){
        if(newCellUnion.getChatId() == null){
            sendException(personId, new RequestException(422, "chat.null"));
            return true;
        }

        if(
            newCellUnion.getXcoordRightBottom() == null ||
            newCellUnion.getXcoordLeftTop() == null ||
            newCellUnion.getYcoordLeftTop() == null ||
            newCellUnion.getYcoordRightBottom() == null
        ){
            sendException(personId, new RequestException(400, "coordinates.null"));
            return true;
        }
        return false;
    }

    public boolean hasAccessToEditCellUnion(BigDecimal personId, CellUnion cellUnion){
        if(cellUnion.getId() == null){
            sendException(personId, new SendIsNotAllowedException());
            return false;
        }
        if(checkForNullability(personId, cellUnion)) return false;

        Optional<TableParticipants> participance = participantsRepository.findById(new TableParticipant(
            cellUnion.getChatId(),
            personId
        ));

        if(participance.isEmpty()){
            sendException(personId, new IllegalAccessException("user.not_in_chat"));
            return false;
        }
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
        if(!rights.contains(Right.EditOwnCellUnions)){
            sendException(personId, new IllegalAccessException("cell_union.edit"));
            return false; // user does not have right to edit any cell unions
        }

        if(samePerson(existingCellUnion.getCreatorId(), personId)){
            if(doesNotIntersect(personId, newCellUnion, existingCellUnion.getId())){
                return true;
            }
            sendException(personId, new CellUnionAlreadyExistsException());
            return false;
        }
        // cell union is not person's. Also, person can't modify other's cell unions
        sendException(personId, new IllegalAccessException("cell_union.edit"));
        return false;
    }

    private boolean doesNotIntersect(BigDecimal personId, CellUnion cellUnion, Long existingCellUnionId){
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
                sendException(personId, new IllegalAccessException("chat.override_other_cell_union"));
                return false;
            }
            if(TableHelp.isInside(intersected, cellUnion) && !cellUnion.getId().equals(existingCellUnionId)){
                inside.add(intersected);
            }
        }
        cellUnionRepository.deleteAll(inside);
        return true;
    }

    public boolean hasAccessToDecorateCellUnion(@NotNull BigDecimal personId, @NotNull CellUnion cellUnion){
        Optional<TableParticipants> participance = participantsRepository.findById(new TableParticipant(
            cellUnion.getChatId(),
            personId
        ));

        if(participance.isEmpty()){
            sendException(personId, new IllegalAccessException("user.not_in_chat"));
            return false;
        }
        var rights = participance.get().rights();
        if(rights.contains(Right.EditOthersCellUnions))
            return true;
        if(!rights.contains(Right.EditOwnCellUnions))
            return false;
        return samePerson(cellUnion.getCreatorId(), personId);
    }
}
