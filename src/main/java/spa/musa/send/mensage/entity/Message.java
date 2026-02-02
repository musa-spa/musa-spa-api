package spa.musa.send.mensage.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma mensagem individual
 */
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @Column(nullable = false)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Direction direction;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.TEXT;

    private String mediaUrl;

    @Column(name = "whatsapp_id")
    private String whatsappId; // ID da mensagem no WhatsApp

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus = DeliveryStatus.SENT;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Enums
    public enum Direction {
        INCOMING,  // Recebida do cliente
        OUTGOING   // Enviada por nós
    }

    public enum MessageType {
        TEXT,
        IMAGE,
        VIDEO,
        AUDIO,
        DOCUMENT,
        LOCATION,
        CONTACT
    }

    public enum DeliveryStatus {
        PENDING,
        SENT,
        DELIVERED,
        READ,
        FAILED
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Conversation getConversation() { return conversation; }
    public void setConversation(Conversation conversation) { this.conversation = conversation; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public String getWhatsappId() { return whatsappId; }
    public void setWhatsappId(String whatsappId) { this.whatsappId = whatsappId; }

    public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
