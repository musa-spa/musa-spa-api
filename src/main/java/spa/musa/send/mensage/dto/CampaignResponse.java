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
@Schema(description = "Resposta com dados da campanha")
public class CampaignResponse {

    private Long id;
    private String title;
    private String message;
    private String status;
    private String targetAudience;
    private String sentDate;
    private String scheduledDate;
    private Integer recipients;
    private Integer opens;
}
