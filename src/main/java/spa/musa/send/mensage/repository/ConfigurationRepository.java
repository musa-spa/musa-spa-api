package spa.musa.send.mensage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spa.musa.send.mensage.entity.Configuration;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    Optional<Configuration> findByKey(String key);

    List<Configuration> findByGroup(String group);

    void deleteByKey(String key);
}
