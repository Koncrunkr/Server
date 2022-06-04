package ru.comgrid.server.service.table;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.comgrid.server.controller.table.InvitationLinkRequest;
import ru.comgrid.server.controller.table.InvitationSuccessResponse;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.exception.InvalidLinkException;
import ru.comgrid.server.exception.RequestException;
import ru.comgrid.server.model.Invitation;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.model.TableParticipants;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.repository.InvitationRepository;
import ru.comgrid.server.service.AccessService;
import ru.comgrid.server.util.EnumSet0;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class InvitationService{
    private final InvitationRepository invitationRepository;
    private final ChatParticipantsRepository participantsRepository;
    private final AccessService accessService;

    public InvitationService(
        @Autowired InvitationRepository invitationRepository,
        @Autowired ChatParticipantsRepository participantsRepository,
        AccessService accessService
    ){
        this.invitationRepository = invitationRepository;
        this.participantsRepository = participantsRepository;
        this.accessService = accessService;
    }

    @NotNull
    public InvitationSuccessResponse acceptInvitation(InvitationLinkRequest code, BigDecimal userId){
        Invitation invitation = invitationRepository.findByInvitationCode(code.getCode())
            .orElseThrow(InvalidLinkException::new);

        if(participantsRepository.existsByChatAndPerson(invitation.getChatId(), userId)){
            throw new RequestException(422, "user.already_participant");
        }

        participantsRepository.save(
            new TableParticipants(
                invitation.getChatId(),
                userId,
                EnumSet0.of(Right.Read, Right.SendMessages, Right.EditOwnMessages, Right.CreateCellUnions, Right.EditOwnCellUnions),
                LocalDateTime.now()
            )
        );
        return new InvitationSuccessResponse(invitation.getChatId());
    }

    @NotNull
    public InvitationLinkRequest getOrCreateInvitationLink(long chatId, BigDecimal userId){
        if(!accessService.hasAccessTo(userId, chatId, Right.AddUsers)){
            throw new IllegalAccessException("manage_users");
        }

        Invitation invitation = invitationRepository.findByChatId(chatId);
        if(invitation == null){
            invitation = invitationRepository.save(new Invitation(chatId));
        }

        return new InvitationLinkRequest(invitation.getInvitationCode());
    }

    public void revokeInvitationLink(long chatId, BigDecimal userId){
        if(!accessService.hasAccessTo(userId, chatId, Right.AddUsers)){
            throw new IllegalAccessException("manage_users");
        }

        invitationRepository.deleteByChatId(chatId);
    }
}
