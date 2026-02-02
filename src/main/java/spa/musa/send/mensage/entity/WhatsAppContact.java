package spa.musa.send.mensage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "whatsapp_contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsAppContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ContactStatus status = ContactStatus.ATIVO;

    private String stage;

    @Column(name = "last_message")
    private LocalDateTime lastMessage;

    @Builder.Default
    private Integer unread = 0;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ContactStatus {
        ATIVO,
        HUMANO,
        AGUARDANDO,
        FINALIZADO
    }
}
