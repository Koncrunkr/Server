package ru.comgrid.server.service.cell;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.comgrid.server.controller.message.MessageUnionRequest;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.exception.OutOfBoundsRequestException;
import ru.comgrid.server.model.CellUnion;
import ru.comgrid.server.model.Chat;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.repository.CellUnionRepository;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.service.AccessService;

import java.math.BigDecimal;
import java.util.List;

import static ru.comgrid.server.service.table.TableHelp.bordersWrong;

@Service
public class CellUnionService{
    private final AccessService accessService;
    private final ChatRepository chatRepository;
    private final CellUnionRepository cellUnionRepository;

    public CellUnionService(
        @Autowired AccessService accessService,
        @Autowired ChatRepository chatRepository,
        @Autowired CellUnionRepository cellUnionRepository
    ){
        this.accessService = accessService;
        this.chatRepository = chatRepository;
        this.cellUnionRepository = cellUnionRepository;
    }

    @NotNull
    public ResponseEntity<List<CellUnion>> getCellUnionsOfChat(long chatId, int xcoordLeftTop, int ycoordLeftTop, int xcoordRightBottom, int ycoordRightBottom, @NotNull BigDecimal userId){
        if(!accessService.hasAccessTo(userId, chatId, Right.Read)){
            throw new IllegalAccessException("chat.read_messages");
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent") // we know it, because user has rights in this chat
        Chat chat = chatRepository.findById(chatId).get();

        if(bordersWrong(chat, new MessageUnionRequest(chatId, xcoordLeftTop, ycoordLeftTop, xcoordRightBottom, ycoordRightBottom))){
            throw new OutOfBoundsRequestException("chat.out_of_bounds");
        }

        return ResponseEntity.ok(
            cellUnionRepository.findAllByChat(
                chatId,
                xcoordLeftTop,
                ycoordLeftTop,
                xcoordRightBottom,
                ycoordRightBottom
            ));
    }
}
