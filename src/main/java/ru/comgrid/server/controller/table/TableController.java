package ru.comgrid.server.controller.table;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.comgrid.server.model.Chat;
import ru.comgrid.server.security.annotation.CurrentUser;
import ru.comgrid.server.security.user.info.UserPrincipal;
import ru.comgrid.server.service.table.InvitationService;
import ru.comgrid.server.service.table.TableService;
import ru.comgrid.server.service.user.UserHelp;

import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * Table service, that has most commonly used table targeted endpoints.
 * (frontend must not specify any credentials since
 * it is done automatically if person is authorized)
 *
 * @author MediaNik
 */
@RestController
@RequestMapping(value = "/table", produces = "application/json; charset=utf-8")
@SecurityRequirement(name = "bearerAuth")
public class TableController{
    private final InvitationService invitationService;
    private final TableService tableService;

    public TableController(
        @Autowired InvitationService invitationService,
        @Autowired TableService tableService
    ){
        this.invitationService = invitationService;
        this.tableService = tableService;
    }

    @ApiResponse(responseCode = "500", description = "image.uploading_error. Internal server error whilst uploading image")
    @ApiResponse(responseCode = "400", description = "image.empty. Provided image was empty.")
    @ApiResponse(responseCode = "400", description = "link.invalid. Provided link for image was invalid.")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public Chat createTable(
        @CurrentUser UserPrincipal user,
        @ModelAttribute @Valid NewChat newChat
    ){
        var userId = UserHelp.extractId(user);
        return tableService.createChat(newChat, userId);
    }

    @ApiResponse(description = "chat.not_found. Chat not found", responseCode = "404")
    @GetMapping("/info")
    public Chat infoAboutTable(
        @CurrentUser UserPrincipal user,
        @RequestParam long chatId,
        @RequestParam(required = false, defaultValue = "false") boolean includeParticipants
    ){
        var userId = UserHelp.extractId(user);
        return tableService.getChat(chatId, includeParticipants, userId);
    }

    @ApiResponse(
        responseCode = "400",
        description = "user.already_participant. Can't add user to chat, because it is already a participant of."
    )
    @ApiResponse(
        responseCode = "404",
        description = "user.not_found. User was not found"
    )
    @ApiResponse(
        responseCode = "403",
        description = "access.add_participant. Can't add user, because you don't have enough rights"
    )
    @ApiResponse(
        responseCode = "200",
        description = "User was added"
    )
    @PostMapping("/add_participant")
    @Transactional
    public void addParticipantToChat(
        @CurrentUser UserPrincipal user,
        @Valid @RequestBody AddParticipantRequest addParticipantRequest
    ){
        var adminUserId = UserHelp.extractId(user);
        tableService.addParticipantToChat(addParticipantRequest, adminUserId);
    }

    @ApiResponse(
        responseCode = "403",
        description = "access.manage_users. Can't change rights of user, because you don't have enough rights to do so."
    )
    @ApiResponse(
        responseCode = "404",
        description = "user.not_found. Provided user does not exist."
    )
    @ApiResponse(
        responseCode = "404",
        description = "user.not_in_chat. Provided user is not in chat."
    )
    @PostMapping("/rights")
    @Operation(summary = "Change rights of user")
    @Transactional
    public void changeRights(
        @CurrentUser UserPrincipal user,
        @Valid @RequestBody ChangeRightsRequest changeRightsRequest
    ){
        var adminUserId = UserHelp.extractId(user);
        var existingUserId = new BigDecimal(changeRightsRequest.userId);

        tableService.changeRightsOfUser(changeRightsRequest, adminUserId, existingUserId);
    }

    @ApiResponse(
        responseCode = "422",
        description = "user.already_participant. User is already a participant of this chat."
    )
    @ApiResponse(
        responseCode = "422",
        description = "link.invalid. Provided link is invalid."
    )
    @Operation(summary = "Enter chat by invitation code")
    @Transactional
    @PostMapping("/invitation_link")
    public InvitationSuccessResponse acceptInvitation(
        @CurrentUser UserPrincipal user,
        @RequestBody InvitationLinkRequest code
    ){
        var userId = UserHelp.extractId(user);
        return invitationService.acceptInvitation(code, userId);
    }

    @ApiResponse(
        responseCode = "403",
        description = "access.manage_users. Can't create invitation link, because you don't have enough rights to do so."
    )
    @Operation(summary = "Get invitation code or create it. Only if you have right AddUser")
    @Transactional
    @GetMapping("/invitation_link")
    public InvitationLinkRequest getInvitationLink(
        @CurrentUser UserPrincipal user,
        @RequestParam long chatId,
        @RequestParam(required = false, defaultValue = "true") boolean createIfNone
    ){
        var userId = UserHelp.extractId(user);

        return invitationService.getOrCreateInvitationLink(chatId, userId, createIfNone);
    }

    @ApiResponse(
        responseCode = "403",
        description = "access.manage_users. Can't revoke invitation link, because you don't have enough rights to do so."
    )
    @Operation(summary = "Revoke invitation code. Only if you have right AddUser")
    @Transactional
    @DeleteMapping("/invitation_link")
    public void revokeInvitationLink(
        @CurrentUser UserPrincipal user,
        @RequestParam long chatId
    ){
        var userId = UserHelp.extractId(user);

        invitationService.revokeInvitationLink(chatId, userId);
    }
}






