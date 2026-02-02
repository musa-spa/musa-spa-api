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
@Schema(description = "Resposta de login")
public class LoginResponse {

    @Schema(description = "Se o login foi bem sucedido")
    private Boolean success;

    @Schema(description = "Mensagem de resposta")
    private String message;

    @Schema(description = "Nome do usuário")
    private String userName;

    @Schema(description = "Token de acesso (para implementação futura)")
    private String token;
}
