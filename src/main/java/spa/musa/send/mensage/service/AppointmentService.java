package spa.musa.send.mensage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spa.musa.send.mensage.dto.AppointmentRequest;
import spa.musa.send.mensage.dto.AppointmentResponse;
import spa.musa.send.mensage.entity.Appointment;
import spa.musa.send.mensage.entity.Client;
import spa.musa.send.mensage.entity.SpaService;
import spa.musa.send.mensage.repository.AppointmentRepository;
import spa.musa.send.mensage.repository.ClientRepository;
import spa.musa.send.mensage.repository.SpaServiceRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final SpaServiceRepository spaServiceRepository;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse findById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        return toResponse(appointment);
    }

    public List<AppointmentResponse> findByDate(LocalDate date) {
        return appointmentRepository.findByDateOrderByTime(date).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByDateRangeOrderByDateTime(startDate, endDate).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> findByClient(Long clientId) {
        return appointmentRepository.findByClientId(clientId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        SpaService service = spaServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        LocalTime time = LocalTime.parse(request.getTime(), TIME_FORMAT);

        Appointment appointment = Appointment.builder()
                .client(client)
                .service(service)
                .date(request.getDate())
                .time(time)
                .room(request.getRoom())
                .status(parseStatus(request.getStatus()))
                .notes(request.getNotes())
                .build();

        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        SpaService service = spaServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        LocalTime time = LocalTime.parse(request.getTime(), TIME_FORMAT);

        appointment.setClient(client);
        appointment.setService(service);
        appointment.setDate(request.getDate());
        appointment.setTime(time);
        appointment.setRoom(request.getRoom());
        appointment.setStatus(parseStatus(request.getStatus()));
        appointment.setNotes(request.getNotes());

        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, String status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        appointment.setStatus(parseStatus(status));
        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Agendamento não encontrado");
        }
        appointmentRepository.deleteById(id);
    }

    public Long countToday() {
        return appointmentRepository.countByDate(LocalDate.now());
    }

    public Long countConfirmedToday() {
        return appointmentRepository.countConfirmedByDate(LocalDate.now());
    }

    private Appointment.AppointmentStatus parseStatus(String status) {
        if (status == null) {
            return Appointment.AppointmentStatus.PENDENTE;
        }
        try {
            return Appointment.AppointmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Appointment.AppointmentStatus.PENDENTE;
        }
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .date(appointment.getDate())
                .time(appointment.getTime().format(TIME_FORMAT))
                .client(appointment.getClient().getName())
                .clientId(appointment.getClient().getId())
                .service(appointment.getService().getName())
                .serviceId(appointment.getService().getId())
                .room(appointment.getRoom())
                .status(appointment.getStatus().name().toLowerCase())
                .notes(appointment.getNotes())
                .build();
    }
}
