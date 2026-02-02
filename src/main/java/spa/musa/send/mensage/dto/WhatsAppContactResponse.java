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
@Schema(description = "Contato do WhatsApp")
public class WhatsAppContactResponse {

    private Long id;
    private String name;
    private String phone;
    private String status;
    private String stage;
    private String lastMessage;
    private String lastSeen;
    private Integer unread;
    private Boolean isClient;
}
