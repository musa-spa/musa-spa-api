package spa.musa.send.mensage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para criar ou atualizar um agendamento")
public class AppointmentRequest {

    @NotNull(message = "ID do cliente é obrigatório")
    @Schema(description = "ID do cliente", example = "1")
    private Long clientId;

    @NotNull(message = "ID do serviço é obrigatório")
    @Schema(description = "ID do serviço", example = "1")
    private Long serviceId;

    @NotNull(message = "Data é obrigatória")
    @Schema(description = "Data do agendamento", example = "2026-01-25")
    private LocalDate date;

    @NotBlank(message = "Horário é obrigatório")
    @Schema(description = "Horário do agendamento", example = "09:00")
    private String time;

    @NotBlank(message = "Sala é obrigatória")
    @Schema(description = "Sala do atendimento", example = "Sala 1")
    private String room;

    @Schema(description = "Status do agendamento", example = "CONFIRMADO")
    private String status;

    @Schema(description = "Observações", example = "Cliente pediu para ligar antes")
    private String notes;
}
