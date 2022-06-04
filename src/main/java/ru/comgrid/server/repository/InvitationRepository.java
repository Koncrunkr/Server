package ru.comgrid.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.comgrid.server.model.Invitation;

import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long>{
    Invitation findByChatId(Long chatId);

    Optional<Invitation> findByInvitationCode(String code);

    void deleteByChatId(Long chatId);
}
