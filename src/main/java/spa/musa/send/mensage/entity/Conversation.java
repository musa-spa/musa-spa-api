package spa.musa.send.mensage.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma conversa/sessão de atendimento
 */
@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    private String contactName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.BOT;

    @Enumerated(EnumType.STRING)
    private Step currentStep = Step.INICIO;

    private String assignedAgent; // Atendente responsável

    @Column(name = "started_at")
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt = LocalDateTime.now();

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    private String notes; // Anotações do atendente

    // Enums
    public enum Status {
        BOT,              // Sendo atendido pelo bot
        WAITING_AGENT,    // Aguardando atendente humano
        WITH_AGENT,       // Com atendente humano
        CLOSED            // Finalizado
    }

    public enum Step {
        INICIO,
        AGUARDANDO_OPCAO,
        AGUARDANDO_ATENDIMENTO,
        FALANDO_COM_HUMANO
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Step getCurrentStep() { return currentStep; }
    public void setCurrentStep(Step currentStep) { this.currentStep = currentStep; }

    public String getAssignedAgent() { return assignedAgent; }
    public void setAssignedAgent(String assignedAgent) { this.assignedAgent = assignedAgent; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
