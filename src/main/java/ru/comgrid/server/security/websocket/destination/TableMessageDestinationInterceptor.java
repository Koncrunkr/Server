package ru.comgrid.server.security.websocket.destination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.model.TableParticipant;
import ru.comgrid.server.model.TableParticipants;
import ru.comgrid.server.repository.ChatParticipantsRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class TableMessageDestinationInterceptor implements IndividualDestinationInterceptor{
	private final ChatParticipantsRepository participantsRepository;

	public TableMessageDestinationInterceptor(@Autowired ChatParticipantsRepository participantsRepository){this.participantsRepository = participantsRepository;}

	@Override
	public String destination(){
		return "table_message";
	}

	@Override
	public boolean hasAccess(BigDecimal userId, String destinationId){
		Optional<TableParticipants> participance = participantsRepository.findById(new TableParticipant(
			Long.parseLong(destinationId),
			userId
		));
		if(participance.isEmpty())
			return false;

		return participance.get().rights().contains(Right.Read);
	}
}