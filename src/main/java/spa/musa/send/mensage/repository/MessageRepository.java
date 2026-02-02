package spa.musa.send.mensage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spa.musa.send.mensage.entity.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    List<Message> findByNumberOrderByCreatedAtDesc(String number);

    @Query("SELECT m FROM Message m WHERE m.number = :number ORDER BY m.createdAt DESC LIMIT 50")
    List<Message> findRecentByNumber(String number);
}
