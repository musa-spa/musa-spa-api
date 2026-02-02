package spa.musa.send.mensage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spa.musa.send.mensage.entity.Conversation;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByNumberAndStatusNot(String number, Conversation.Status status);

    Optional<Conversation> findFirstByNumberOrderByStartedAtDesc(String number);

    List<Conversation> findByStatus(Conversation.Status status);

    List<Conversation> findByStatusIn(List<Conversation.Status> statuses);

    @Query("SELECT c FROM Conversation c WHERE c.status = 'WAITING_AGENT' ORDER BY c.lastMessageAt ASC")
    List<Conversation> findWaitingForAgent();

    @Query("SELECT c FROM Conversation c WHERE c.status IN ('BOT', 'WAITING_AGENT', 'WITH_AGENT') ORDER BY c.lastMessageAt DESC")
    List<Conversation> findActiveConversations();

    @Query("SELECT c FROM Conversation c WHERE c.assignedAgent = :agent AND c.status = 'WITH_AGENT'")
    List<Conversation> findByAgent(String agent);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.status = 'WAITING_AGENT'")
    long countWaitingForAgent();
}
