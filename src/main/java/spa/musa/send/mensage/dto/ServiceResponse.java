package spa.musa.send.mensage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com dados do serviço")
public class ServiceResponse {

    private Long id;
    private String name;
    private String description;
    private String category;
    private Integer duration;
    private BigDecimal price;
    private Boolean active;
}
