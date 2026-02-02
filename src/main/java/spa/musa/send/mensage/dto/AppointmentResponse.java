package spa.musa.send.mensage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com dados do agendamento")
public class AppointmentResponse {

    private Long id;
    private LocalDate date;
    private String time;
    private String client;
    private Long clientId;
    private String service;
    private Long serviceId;
    private String room;
    private String status;
    private String notes;
}
