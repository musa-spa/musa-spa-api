package spa.musa.send.mensage.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import spa.musa.send.mensage.entity.Conversation;

/**
 * Serviço que processa mensagens recebidas e delega para o ChatFlowService
 */
@Service
public class MessageHandlerService {

    private static final Logger log = LoggerFactory.getLogger(MessageHandlerService.class);

    private final ChatFlowService chatFlowService;
    private final ConversationService conversationService;

    public MessageHandlerService(ChatFlowService chatFlowService, ConversationService conversationService) {
        this.chatFlowService = chatFlowService;
        this.conversationService = conversationService;
    }

    /**
     * Processa mensagem recebida e responde conforme o fluxo
     */
    public void handleIncomingMessage(String number, String name, String message, JsonNode event) {
        if (message == null || message.isBlank()) {
            log.debug("Mensagem vazia ignorada");
            return;
        }

        // Obtém ou cria conversa no banco (para histórico)
        Conversation conversation = conversationService.getOrCreateConversation(number, name);

        // Salva mensagem recebida
        String whatsappId = event.path("data").path("key").path("id").asText(null);
        conversationService.saveIncomingMessage(conversation, message, whatsappId);

        log.info("📩 Processando mensagem de {} | Mensagem: {}", number, message);

        // Delega para o ChatFlowService que gerencia o fluxo de menus
        try {
            chatFlowService.processMessage(number, name, message);
        } catch (Exception e) {
            log.error("Erro ao processar fluxo de chat para {}: {}", number, e.getMessage(), e);
        }
    }
}
