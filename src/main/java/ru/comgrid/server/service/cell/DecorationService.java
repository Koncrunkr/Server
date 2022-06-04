package ru.comgrid.server.service.cell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.comgrid.server.controller.decoration.DecorationRequest;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.model.Decoration;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.repository.DecorationRepository;
import ru.comgrid.server.service.AccessService;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DecorationService{
    private final DecorationRepository decorationRepository;
    private final AccessService accessService;

    public DecorationService(
        @Autowired DecorationRepository decorationRepository,
        @Autowired AccessService accessService
    ){
        this.decorationRepository = decorationRepository;
        this.accessService = accessService;
    }

    public List<Decoration> getDecorationList(BigDecimal userId, DecorationRequest decorationRequest){
        if(!accessService.hasAccessTo(userId, decorationRequest.getChatId(), Right.Read))
            throw new IllegalAccessException("chat.read_messages");

        return decorationRepository.findAllByCellUnionIdInAndAndChatId(
            decorationRequest.getCellUnionIds(),
            decorationRequest.getChatId()
        );
    }
}
