package spa.musa.send.mensage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Estatísticas do dashboard")
public class DashboardStatsResponse {

    @Schema(description = "Total de agendamentos hoje")
    private Integer appointmentsToday;

    @Schema(description = "Total de clientes cadastrados")
    private Integer totalClients;

    @Schema(description = "Conversas ativas")
    private Integer activeConversations;

    @Schema(description = "Taxa de confirmação")
    private Integer confirmationRate;
    
    @Schema(description = "Agendamentos pendentes")
    private Integer pendingAppointments;
    
    @Schema(description = "Completados hoje")
    private Integer completedToday;
}
