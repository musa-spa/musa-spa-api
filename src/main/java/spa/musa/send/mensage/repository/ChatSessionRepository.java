package spa.musa.send.mensage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spa.musa.send.mensage.entity.ChatSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findByPhoneNumber(String phoneNumber);

    @Query("SELECT cs FROM ChatSession cs WHERE cs.menuLevel = 'WAITING_HUMAN'")
    List<ChatSession> findWaitingForHuman();

    @Query("SELECT cs FROM ChatSession cs WHERE cs.lastInteraction < :timeout")
    List<ChatSession> findInactiveSessions(@Param("timeout") LocalDateTime timeout);

    void deleteByPhoneNumber(String phoneNumber);
}
