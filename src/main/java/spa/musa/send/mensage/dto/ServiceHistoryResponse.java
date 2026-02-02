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
@Schema(description = "Histórico de serviço do cliente")
public class ServiceHistoryResponse {

    private Long id;
    private String date;
    private String service;
    private BigDecimal price;
    private String status;
}
