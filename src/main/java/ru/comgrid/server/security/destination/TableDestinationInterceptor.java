package ru.comgrid.server.security.destination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.comgrid.server.repository.ChatParticipantsRepository;

import java.math.BigDecimal;

@Component
public class TableDestinationInterceptor implements IndividualDestinationInterceptor{

    private final ChatParticipantsRepository participantsRepository;

    public TableDestinationInterceptor(@Autowired ChatParticipantsRepository participantsRepository){this.participantsRepository = participantsRepository;}

    @Override
    public String destination(){
        return "table";
    }

    @Override
    public boolean hasAccess(BigDecimal userId, String destinationId){
        return participantsRepository.existsByChatAndPerson(Long.valueOf(destinationId), userId);
    }
}
