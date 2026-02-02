package spa.musa.send.mensage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spa.musa.send.mensage.entity.Campaign;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByStatus(Campaign.CampaignStatus status);

    List<Campaign> findByTargetAudience(String targetAudience);

    List<Campaign> findAllByOrderByCreatedAtDesc();
}
