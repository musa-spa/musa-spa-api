package spa.musa.send.mensage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spa.musa.send.mensage.dto.WhatsAppContactResponse;
import spa.musa.send.mensage.dto.WhatsAppKpisResponse;
import spa.musa.send.mensage.dto.WhatsAppStatusResponse;
import spa.musa.send.mensage.entity.WhatsAppContact;
import spa.musa.send.mensage.repository.ClientRepository;
import spa.musa.send.mensage.repository.WhatsAppContactRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private final WhatsAppContactRepository whatsAppContactRepository;
    private final ClientRepository clientRepository;
    
    @Value("${evolution.instance.name:musa spa}")
    private String instanceName;

    public WhatsAppKpisResponse getKpis() {
        Long contactsInFlow = whatsAppContactRepository.count() - whatsAppContactRepository.countHumanAttendance();
        Long humanAttendance = whatsAppContactRepository.countHumanAttendance();
        Long activeConversations = whatsAppContactRepository.countActiveConversations();
        Long finishedToday = whatsAppContactRepository.countFinishedToday();

        return WhatsAppKpisResponse.builder()
                .contactsInFlow(contactsInFlow.intValue())
                .humanAttendance(humanAttendance.intValue())
                .activeConversations(activeConversations.intValue())
                .finishedToday(finishedToday.intValue())
                // Campos extras esperados pelo frontend
                .totalMessages(activeConversations.intValue() * 5) // Estimativa
                .sentMessages(activeConversations.intValue() * 3)
                .receivedMessages(activeConversations.intValue() * 2)
                .activeChats(activeConversations.intValue())
                .build();
    }

    public List<WhatsAppContactResponse> getActiveContacts() {
        return whatsAppContactRepository.findByStatusNot(WhatsAppContact.ContactStatus.FINALIZADO)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<WhatsAppContactResponse> getAllContacts() {
        return whatsAppContactRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WhatsAppStatusResponse getStatus() {
        // TODO: Verificar conexão real com a API Evolution
        return WhatsAppStatusResponse.builder()
                .connected(true)
                .instanceName(instanceName)
                .phoneNumber("+55 11 94979-1718")
                .lastSync(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                .qrCode(null)
                .build();
    }

    private WhatsAppContactResponse toResponse(WhatsAppContact contact) {
        // Verifica se o contato é um cliente cadastrado
        boolean isClient = clientRepository.existsByPhone(contact.getPhone());
        
        return WhatsAppContactResponse.builder()
                .id(contact.getId())
                .name(contact.getName())
                .phone(contact.getPhone())
                .status(contact.getStatus().name().toLowerCase())
                .stage(contact.getStage())
                .lastMessage(formatLastMessage(contact.getLastMessage()))
                .lastSeen(contact.getLastMessage() != null ? 
                    contact.getLastMessage().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .unread(contact.getUnread())
                .isClient(isClient)
                .build();
    }

    private String formatLastMessage(LocalDateTime lastMessage) {
        if (lastMessage == null) {
            return "N/A";
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(lastMessage, now);

        if (minutes < 1) {
            return "agora";
        } else if (minutes < 60) {
            return minutes + " min";
        } else if (minutes < 1440) {
            return (minutes / 60) + "h";
        } else {
            return (minutes / 1440) + "d";
        }
    }
}
