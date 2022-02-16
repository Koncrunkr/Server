package ru.comgrid.server.api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.model.TableParticipant;
import ru.comgrid.server.model.TableParticipants;
import ru.comgrid.server.repository.ChatParticipantsRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccessService{

    private final ChatParticipantsRepository participantsRepository;

    public AccessService(@Autowired ChatParticipantsRepository participantsRepository){this.participantsRepository = participantsRepository;}

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
}
