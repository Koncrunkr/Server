package ru.comgrid.server.repository;

import org.hibernate.annotations.NamedNativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.comgrid.server.model.Message;
import ru.comgrid.server.model.MessageId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends CrudRepository<Message, MessageId>{
    Optional<Message> findMessageByChatIdAndXAndY(@Param("chatId") Long chatId, @Param("x") Integer x, @Param("y") Integer y);

    @Query("""
        select m from Message m
        where m.chatId = :chatId and m.x between :startX and :endX and m.y between :startY and :endY
        order by m.edited DESC""")
    List<Message> findAllInChat(
        @Param("chatId") Long chatId,
        @Param("startX") Integer startX,
        @Param("endX") Integer endX,
        @Param("startY") Integer startY,
        @Param("endY") Integer endY
    );

    @Query("""
        select m from Message m
        where m.chatId = :chatId and m.x between :startX and :endX and m.y between :startY and :endY and m.edited between :startTime and :endTime
        order by m.edited DESC""")
    List<Message> findAllInChatBetween(
        @Param("chatId") Long chatId,
        @Param("startX") Integer startX,
        @Param("endX") Integer endX,
        @Param("startY") Integer startY,
        @Param("endY") Integer endY,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query(
        nativeQuery = true,
        value = """
        select * from message
        where message.text %> :text and message.chat_id=:chatId
        order by message.edited desc offset :offset limit :limit
        """)
    List<Message> findSimilarInChat(
        @Param("chatId") long chatId,
        @Param("text") String text,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    @Query(
        nativeQuery = true,
        value = """
        select message.* from message
        inner join table_participants on message.chat_id = table_participants.chat
        where text %> :text and table_participants.person=:personId and table_participants.rights & 1 = 1
        order by message.edited desc offset :offset limit :limit
        """)
    List<Message> findSimilar(
        @Param("personId") BigDecimal personId,
        @Param("text") String text,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    /**
     * You must assert that
     * @param personId
     * @param text
     * @param offset
     * @param limit
     * @param startTime
     * @param endTime
     * @return
     */
    @Query(
        nativeQuery = true,
        value = """
        select message.* from message
        inner join table_participants on message.chat_id = table_participants.chat
        where text %> :text and table_participants.person=:personId and table_participants.rights & 1 = 1
        and message.edited > :startTime and message.edited < :endTime
        order by message.created desc offset :offset limit :limit
        """)
    List<Message> findSimilarForPeriod(
        @Param("personId") BigDecimal personId,
        @Param("text") String text,
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query(
        nativeQuery = true,
        value = """
        select message.* from message
        where message.text %> :text and message.chat_id=:chatId
        and message.edited > :startTime and message.edited < :endTime
        order by message.created desc offset :offset limit :limit
        """)
    List<Message> findSimilarInChatForPeriod(
        @Param("chatId") long chatId,
        @Param("text") String text,
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query(
        nativeQuery = true,
        value = """
        select message.* from message
        where message.text ~* ('\\y' || :text || '\\y') and message.chat_id=:chatId
        order by message.created desc offset :offset limit :limit
        """
    )
    List<Message> findExactInChat(
        @Param("chatId") long chatId,
        @Param("text") String text,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    @Query(
        nativeQuery = true,
        value = """
        select message.* from message
        where message.text ~* ('\\y' || :text || '\\y') and message.chat_id=:chatId
        and message.edited > :startTime and message.edited < :endTime
        order by message.created desc offset :offset limit :limit
        """
    )
    List<Message> findExactInChatForPeriod(
        @Param("chatId") long chatId,
        @Param("text") String text,
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    @Query(
        nativeQuery = true,
        value = """
        select message.* from message
        inner join table_participants on message.chat_id = table_participants.chat
        where message.text ~* ('\\y' || :text || '\\y') and table_participants.person=:personId and table_participants.rights & 1 = 1
        order by message.created desc offset :offset limit :limit
        """
    )
    List<Message> findExact(
        @Param("personId") BigDecimal personId,
        @Param("text") String text,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
    @Query(
        nativeQuery = true,
        value = """
        select message.* from message
        inner join table_participants on message.chat_id = table_participants.chat
        where message.text ~* ('\\y' || :text || '\\y') and table_participants.person=:personId and table_participants.rights & 1 = 1
        and message.edited > :startTime and message.edited < :endTime
        order by message.created desc offset :offset limit :limit
        """
    )
    List<Message> findExactForPeriod(
        @Param("personId") BigDecimal personId,
        @Param("text") String text,
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}
