package spa.musa.send.mensage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spa.musa.send.mensage.entity.Contact;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByNumber(String number);

    List<Contact> findByOptedInTrue();

    @Query("SELECT c FROM Contact c WHERE c.tags LIKE %:tag%")
    List<Contact> findByTag(String tag);

    @Query("SELECT c FROM Contact c WHERE c.optedIn = true ORDER BY c.lastInteraction DESC")
    List<Contact> findActiveContacts();
}
