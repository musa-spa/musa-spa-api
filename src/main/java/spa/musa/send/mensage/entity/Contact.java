package spa.musa.send.mensage.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade que representa um contato/número de WhatsApp
 */
@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    private String name;

    private String email;

    @Column(name = "opted_in")
    private Boolean optedIn = true; // Autorizou receber mensagens

    private String tags; // "cliente,vip,promocoes" - separado por vírgula

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_interaction")
    private LocalDateTime lastInteraction;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getOptedIn() { return optedIn; }
    public void setOptedIn(Boolean optedIn) { this.optedIn = optedIn; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastInteraction() { return lastInteraction; }
    public void setLastInteraction(LocalDateTime lastInteraction) { this.lastInteraction = lastInteraction; }
}
