package spa.musa.send.mensage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para criar ou atualizar uma campanha")
public class CampaignRequest {

    @NotBlank(message = "Título é obrigatório")
    @Schema(description = "Título da campanha", example = "Promoção de Verão")
    private String title;

    @NotBlank(message = "Mensagem é obrigatória")
    @Schema(description = "Mensagem da campanha", example = "Aproveite 20% de desconto em todos os tratamentos faciais!")
    private String message;

    @NotBlank(message = "Público-alvo é obrigatório")
    @Schema(description = "Público-alvo", example = "all")
    private String targetAudience;

    @Schema(description = "Data agendada para envio")
    private LocalDateTime scheduledDate;

    @Schema(description = "Salvar como rascunho", example = "false")
    @Builder.Default
    private Boolean asDraft = false;
}
