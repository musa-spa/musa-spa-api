package spa.musa.send.mensage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para criar ou atualizar um cliente")
public class ClientRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome completo do cliente", example = "Maria Silva")
    private String name;

    @NotBlank(message = "Telefone é obrigatório")
    @Schema(description = "Telefone do cliente", example = "(11) 99999-1111")
    private String phone;

    @Email(message = "E-mail inválido")
    @Schema(description = "E-mail do cliente", example = "maria@email.com")
    private String email;

    @Schema(description = "Observações sobre o cliente", example = "Cliente VIP, prefere horários pela manhã")
    private String notes;
}
