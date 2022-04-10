package ru.comgrid.server.security.destination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.comgrid.server.api.user.AccessService;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.model.TableParticipant;
import ru.comgrid.server.model.TableParticipants;
import ru.comgrid.server.repository.ChatParticipantsRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class CellUnionDestinationInterceptor implements IndividualDestinationInterceptor{
	private final ChatParticipantsRepository participantsRepository;

	public CellUnionDestinationInterceptor(
		@Autowired ChatParticipantsRepository participantsRepository
	){
		this.participantsRepository = participantsRepository;
	}
	@Override
	public String destination(){
		return "table_cell_union";
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
