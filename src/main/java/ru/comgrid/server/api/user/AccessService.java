package ru.comgrid.server.api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.comgrid.server.api.table.TableHelp;
import ru.comgrid.server.model.*;
import ru.comgrid.server.repository.CellUnionRepository;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.util.EnumSet0;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.comgrid.server.api.user.UserHelp.samePerson;

@Service
public class AccessService{

    private final ChatParticipantsRepository participantsRepository;
    private final CellUnionRepository cellUnionRepository;
    private final int defaultPageSize;

    public AccessService(
        @Autowired ChatParticipantsRepository participantsRepository,
        @Autowired CellUnionRepository cellUnionRepository,
        @Value("${ru.comgrid.chat.default-page-size}") int defaultPageSize
    ){
        this.participantsRepository = participantsRepository;
        this.cellUnionRepository = cellUnionRepository;
        this.defaultPageSize = defaultPageSize;
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

    public boolean hasAccessToSendOrEditMessage(BigDecimal personId, Message message, Optional<Message> existingMessage){
        Long chatId = message.getChatId();
        if(existingMessage.isPresent()){
            if(!isSender(personId, existingMessage.get())){
                if(!hasAccessTo(personId, chatId, Right.OverrideOthersMessages)){
                    return false;
                }
            }
        }
        return hasAccessTo(personId, chatId, Right.Write);
    }

    private static boolean isSender(BigDecimal personId, Message existingMessage){
        return samePerson(existingMessage.getSenderId(), personId);
    }

    public boolean hasAccessToCreateOrEditCellUnion(BigDecimal personId, CellUnion newCellUnion, Optional<CellUnion> existingCellUnionOptional){
        Optional<TableParticipants> participanceOptional = participantsRepository.findById(new TableParticipant(
            newCellUnion.getChatId(),
            personId
        ));

        if(participanceOptional.isEmpty())
            return false;
        var rights = participanceOptional.get().rights();

        if(existingCellUnionOptional.isPresent()){
            return hasAccessToEditCellUnion(personId, newCellUnion, existingCellUnionOptional.get(), rights);
        }
        if(rights.contains(Right.EditOthersCellUnions))
            return true; // user can edit all cell unions
        if(!rights.contains(Right.Write))
            return false; // user does not have right to edit any cell unions
        return doesNotIntersect(personId, newCellUnion, null);
    }

    private boolean hasAccessToEditCellUnion(BigDecimal personId, CellUnion newCellUnion, CellUnion existingCellUnion, EnumSet0<Right> rights){
        if(rights.contains(Right.EditOthersCellUnions))
            return true; // user can edit all cell unions
        if(!rights.contains(Right.EditCellUnions))
            return false; // user does not have right to edit any cell unions

        if(samePerson(existingCellUnion.getCreatorId(), personId)){
            return doesNotIntersect(personId, newCellUnion, existingCellUnion.getId());
        }
        // cell union is not person's. Also, person can't modify other's cell unions
        return false;
    }

    private boolean doesNotIntersect(BigDecimal personId, CellUnion cellUnion, Long existingCellUnionId){
        Page<CellUnion> cellUnionsIntersected = cellUnionRepository.findAllByChat(
            cellUnion.getChatId(),
            cellUnion.getXcoordLeftTop(),
            cellUnion.getYcoordLeftTop(),
            cellUnion.getXcoordRightBottom(),
            cellUnion.getYcoordRightBottom(),
            Pageable.ofSize(defaultPageSize)
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
