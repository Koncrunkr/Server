package ru.comgrid.server.api.decoration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import ru.comgrid.server.api.WebsocketDestination
import ru.comgrid.server.api.user.AccessService
import ru.comgrid.server.api.user.UserHelp
import ru.comgrid.server.exception.NotFoundException
import ru.comgrid.server.exception.RequestException
import ru.comgrid.server.model.Decoration
import ru.comgrid.server.repository.CellUnionRepository
import ru.comgrid.server.repository.DecorationRepository
import ru.comgrid.server.security.annotation.CurrentUser
import ru.comgrid.server.security.user.info.UserPrincipal
import java.math.BigDecimal

@Controller
open class WebsocketDecoration(
    @param:Autowired private val messagingTemplate: SimpMessagingTemplate,
    @param:Autowired private val cellUnionRepository: CellUnionRepository,
    @param:Autowired private val decorationRepository: DecorationRepository,
    @param:Autowired private val accessService: AccessService,
) {

    @Transactional
    @MessageMapping("/table_decoration")
    open fun processNewMessage(
        @CurrentUser user: UserPrincipal,
        @Payload decoration: Decoration,
    ) {
        val personId = UserHelp.extractId(user)
        if(decoration.chatId === null) {
            sendException(personId, RequestException(400, "chat_id.null"))
            return
        }

        if(decoration.id !== null){
            val existingDecoration = decorationRepository.findById(decoration.id)
            if(existingDecoration.isEmpty){
                sendException(personId, NotFoundException("decoration.by_id"))
                return
            }
            if(existingDecoration.get().isNotReplacableBy(decoration, personId)){
                return
            }
        }
        if(!checkCellUnion(personId, decoration)){
            return
        }

        val saved = decorationRepository.save(decoration)
        messagingTemplate.convertAndSend(WebsocketDestination.TABLE_DECORATION.destination(saved.id), saved)
    }

    @Transactional
    open fun checkCellUnion(personId: BigDecimal, decoration: Decoration) : Boolean{
        val cellUnionId = decoration.cellUnionId
        if(cellUnionId === null){
            sendException(personId, RequestException(400, "cell_union.null"))
            return false
        }
        val cellUnion = cellUnionRepository.findById(cellUnionId)
        if(cellUnion.isEmpty){
            sendException(personId, NotFoundException("cell_union.not_found"))
            return false
        }
        if(!accessService.hasAccessToDecorateCellUnion(personId, cellUnion.get())){
            return false
        }
        return true
    }

    private fun sendException(personId: BigDecimal, requestException: RequestException) {
        messagingTemplate.convertAndSend(WebsocketDestination.USER.destination(personId), requestException)
    }
    private fun Decoration.isNotReplacableBy(decoration: Decoration, personId: BigDecimal): Boolean {
        if(chatId != decoration.chatId){
            sendException(personId, RequestException(422, "decoration.in_different_chat"))
            return false
        }
        if(cellUnionId != decoration.cellUnionId){
            sendException(personId, RequestException(422, "decoration.in_different_cell_union"))
            return false
        }
        return true
    }
}

