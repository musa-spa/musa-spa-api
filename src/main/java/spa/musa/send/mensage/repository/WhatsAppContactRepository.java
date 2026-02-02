package spa.musa.send.mensage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spa.musa.send.mensage.entity.WhatsAppContact;

import java.util.List;

@Repository
public interface WhatsAppContactRepository extends JpaRepository<WhatsAppContact, Long> {

    List<WhatsAppContact> findByStatus(WhatsAppContact.ContactStatus status);

    List<WhatsAppContact> findByStatusNot(WhatsAppContact.ContactStatus status);

    @Query("SELECT COUNT(w) FROM WhatsAppContact w WHERE w.status = 'HUMANO'")
    Long countHumanAttendance();

    @Query("SELECT COUNT(w) FROM WhatsAppContact w WHERE w.status IN ('ATIVO', 'AGUARDANDO')")
    Long countActiveConversations();

    @Query("SELECT COUNT(w) FROM WhatsAppContact w WHERE w.status = 'FINALIZADO' AND DATE(w.lastMessage) = CURRENT_DATE")
    Long countFinishedToday();
}
