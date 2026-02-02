package spa.musa.send.mensage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spa.musa.send.mensage.entity.SpaService;

import java.util.List;

@Repository
public interface SpaServiceRepository extends JpaRepository<SpaService, Long> {

    List<SpaService> findByCategory(String category);

    List<SpaService> findByActiveTrue();

    List<SpaService> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    List<SpaService> findByCategoryAndActiveTrue(String category);
}
