package spa.musa.send.mensage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spa.musa.send.mensage.dto.AppointmentResponse;
import spa.musa.send.mensage.dto.DashboardStatsResponse;
import spa.musa.send.mensage.repository.AppointmentRepository;
import spa.musa.send.mensage.repository.ClientRepository;
import spa.musa.send.mensage.repository.WhatsAppContactRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AppointmentService appointmentService;
    private final ClientRepository clientRepository;
    private final AppointmentRepository appointmentRepository;
    private final WhatsAppContactRepository whatsAppContactRepository;

    public DashboardStatsResponse getStats() {
        Long appointmentsToday = appointmentRepository.countByDate(LocalDate.now());
        Long totalClients = clientRepository.count();
        Long activeConversations = whatsAppContactRepository.countActiveConversations();
        
        Long confirmedToday = appointmentRepository.countConfirmedByDate(LocalDate.now());
        Long completedToday = appointmentRepository.countCompletedByDate(LocalDate.now());
        Long pendingToday = appointmentRepository.countPendingByDate(LocalDate.now());
        
        int confirmationRate = appointmentsToday > 0 ? (int) ((confirmedToday * 100) / appointmentsToday) : 0;

        return DashboardStatsResponse.builder()
                .appointmentsToday(appointmentsToday.intValue())
                .totalClients(totalClients.intValue())
                .activeConversations(activeConversations.intValue())
                .confirmationRate(confirmationRate)
                .pendingAppointments(pendingToday.intValue())
                .completedToday(completedToday.intValue())
                .build();
    }

    public List<AppointmentResponse> getUpcomingAppointments() {
        return appointmentService.findByDate(LocalDate.now());
    }
}
