package spa.musa.send.mensage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spa.musa.send.mensage.entity.Client;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByPhone(String phone);

    Optional<Client> findByEmail(String email);

    Optional<Client> findByCpf(String cpf);
    
    boolean existsByPhone(String phone);

    List<Client> findByNameContainingIgnoreCaseOrPhoneContainingOrEmailContainingIgnoreCase(
            String name, String phone, String email);

    @Query("SELECT COUNT(c) FROM Client c WHERE MONTH(c.createdAt) = MONTH(CURRENT_DATE) AND YEAR(c.createdAt) = YEAR(CURRENT_DATE)")
    Long countNewClientsThisMonth();
}
