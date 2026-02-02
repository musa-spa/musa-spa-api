package spa.musa.send.mensage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para criar ou atualizar um serviço")
public class ServiceRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome do serviço", example = "Limpeza de Pele")
    private String name;

    @Schema(description = "Descrição do serviço", example = "Limpeza profunda da pele com extração de cravos e hidratação")
    private String description;

    @NotBlank(message = "Categoria é obrigatória")
    @Schema(description = "Categoria do serviço", example = "Facial")
    private String category;

    @NotNull(message = "Duração é obrigatória")
    @Min(value = 1, message = "Duração deve ser maior que 0")
    @Schema(description = "Duração em minutos", example = "60")
    private Integer duration;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")
    @Schema(description = "Preço do serviço", example = "150.00")
    private BigDecimal price;

    @Builder.Default
    @Schema(description = "Se o serviço está ativo", example = "true")
    private Boolean active = true;
}
