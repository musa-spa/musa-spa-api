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
@Schema(description = "KPIs do WhatsApp")
public class WhatsAppKpisResponse {

    @Schema(description = "Contatos em fluxo")
    private Integer contactsInFlow;

    @Schema(description = "Atendimento humano")
    private Integer humanAttendance;

    @Schema(description = "Conversas ativas")
    private Integer activeConversations;

    @Schema(description = "Finalizados hoje")
    private Integer finishedToday;
    
    @Schema(description = "Total de mensagens hoje")
    private Integer totalMessages;
    
    @Schema(description = "Mensagens enviadas hoje")
    private Integer sentMessages;
    
    @Schema(description = "Mensagens recebidas hoje")
    private Integer receivedMessages;
    
    @Schema(description = "Chats ativos")
    private Integer activeChats;
}
