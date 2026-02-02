package spa.musa.send.mensage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spa.musa.send.mensage.entity.ServiceHistory;

import java.util.List;

@Repository
public interface ServiceHistoryRepository extends JpaRepository<ServiceHistory, Long> {

    List<ServiceHistory> findByClientIdOrderByServiceDateDesc(Long clientId);
}
