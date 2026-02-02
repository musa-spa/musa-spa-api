package spa.musa.send.mensage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CampaignStatus status = CampaignStatus.RASCUNHO;

    @Column(name = "target_audience", nullable = false)
    private String targetAudience;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Builder.Default
    private Integer recipients = 0;

    @Builder.Default
    private Integer opens = 0;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum CampaignStatus {
        RASCUNHO,
        AGENDADA,
        ENVIADA
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
