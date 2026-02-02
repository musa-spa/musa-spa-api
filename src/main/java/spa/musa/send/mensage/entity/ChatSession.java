    package spa.musa.send.mensage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidade para armazenar o estado da sessão de chat e dados temporários do agendamento
 */
@Entity
@Table(name = "chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MenuLevel menuLevel = MenuLevel.MAIN_MENU;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FlowType flowType = FlowType.NONE;

    // Dados temporários do agendamento
    private String selectedCategory;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "selected_service_id")
    private SpaService selectedService;
    
    private LocalDate selectedDate;
    private LocalTime selectedTime;

    // Dados do cliente em coleta
    private String tempName;
    private String tempCpf;
    private String tempPhone;
    private String tempEmail;
    private String tempMessage; // Para suporte/atendente

    // Número do ticket de suporte
    private String ticketNumber;

    // Histórico de navegação (para voltar)
    @Enumerated(EnumType.STRING)
    private MenuLevel previousLevel;

    @Column(name = "last_interaction")
    @Builder.Default
    private LocalDateTime lastInteraction = LocalDateTime.now();

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Enums para controle de fluxo
    public enum MenuLevel {
        // Menu principal
        MAIN_MENU,
        
        // Nível 1 - Serviços
        SERVICES_MENU,
        SERVICES_FACIAL,
        SERVICES_MANICURE,
        SERVICES_CORPORAL,
        SERVICES_CABELO,
        SERVICES_PACOTES,
        SERVICE_DETAIL,  // Visualizando detalhes de um serviço específico
        
        // Nível 2 - Preços
        PRICES_MENU,
        PRICES_FACIAL,
        PRICES_MANICURE,
        PRICES_CORPORAL,
        PRICES_CABELO,
        PRICES_PACOTES,
        
        // Nível 3 - Agendamento
        SCHEDULING_CATEGORY,
        SCHEDULING_SERVICE,
        SCHEDULING_DATE,
        SCHEDULING_TIME,
        SCHEDULING_NAME,
        SCHEDULING_CPF,
        SCHEDULING_PHONE,
        SCHEDULING_EMAIL,
        SCHEDULING_CONFIRMATION,
        
        // Nível 4 - Suporte
        SUPPORT_MENU,
        SUPPORT_PROBLEM,
        SUPPORT_DETAILS,
        SUPPORT_NAME,
        SUPPORT_CPF,
        SUPPORT_PHONE,
        SUPPORT_EMAIL,
        
        // Nível 5 - Atendente
        ATTENDANT_NAME,
        ATTENDANT_CPF,
        ATTENDANT_PHONE,
        ATTENDANT_EMAIL,
        ATTENDANT_MESSAGE,
        ATTENDANT_QUEUE,
        
        // Aguardando humano
        WAITING_HUMAN,
        
        // Fluxo finalizado (aguardando 1 ou 2)
        FLOW_COMPLETED
    }

    public enum FlowType {
        NONE,
        SERVICES,
        PRICES,
        SCHEDULING,
        SUPPORT,
        ATTENDANT
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastInteraction = LocalDateTime.now();
    }

    // Métodos auxiliares
    public void resetSession() {
        this.menuLevel = MenuLevel.MAIN_MENU;
        this.flowType = FlowType.NONE;
        this.selectedCategory = null;
        this.selectedService = null;
        this.selectedDate = null;
        this.selectedTime = null;
        this.tempName = null;
        this.tempCpf = null;
        this.tempPhone = null;
        this.tempEmail = null;
        this.tempMessage = null;
        this.ticketNumber = null;
        this.previousLevel = null;
    }

    public void goBack() {
        if (previousLevel != null) {
            this.menuLevel = previousLevel;
            this.previousLevel = null;
        } else {
            this.menuLevel = MenuLevel.MAIN_MENU;
        }
    }
}
