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
@Schema(description = "Status de integração do WhatsApp")
public class WhatsAppStatusResponse {

    @Schema(description = "Se a API está conectada")
    private Boolean connected;

    @Schema(description = "Nome da instância")
    private String instanceName;
    
    @Schema(description = "Número de telefone conectado")
    private String phoneNumber;
    
    @Schema(description = "Última sincronização")
    private String lastSync;
    
    @Schema(description = "QR Code para conexão")
    private String qrCode;
}
