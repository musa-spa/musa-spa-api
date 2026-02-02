package spa.musa.send.mensage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com dados do cliente")
public class ClientResponse {

    @Schema(description = "ID do cliente")
    private Long id;

    @Schema(description = "Nome do cliente")
    private String name;

    @Schema(description = "Telefone do cliente")
    private String phone;

    @Schema(description = "E-mail do cliente")
    private String email;

    @Schema(description = "Observações")
    private String notes;

    @Schema(description = "Data da última visita")
    private String lastVisit;

    @Schema(description = "Total de visitas")
    private Integer totalVisits;
    
    @Schema(description = "Data de criação")
    private String createdAt;

    @Schema(description = "Histórico de serviços")
    private List<ServiceHistoryResponse> serviceHistory;
}
