package spa.musa.send.mensage.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import spa.musa.send.mensage.service.MessageHandlerService;

@Component
public class WebhookProcessor {

    private static final Logger log = LoggerFactory.getLogger(WebhookProcessor.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final MessageHandlerService messageHandler;

    public WebhookProcessor(MessageHandlerService messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void process(String rawPayload) {
        try {
            JsonNode root = mapper.readTree(rawPayload);

            String event = root.path("event").asText(null);
            String instance = root.path("instance").asText(null);

            if (event == null) {
                log.warn("Webhook sem campo event. Payload: {}", rawPayload);
                return;
            }

            event = event.toUpperCase().replace('.', '_');
            log.info("📥 Evento recebido: {} | instância: {}", event, instance);

            switch (event) {
                case "MESSAGES_UPSERT" -> handleNewMessage(root);
                case "MESSAGES_UPDATE" -> handleMessageUpdate(root);
                case "CONNECTION_UPDATE" -> handleConnectionUpdate(root);
                case "QRCODE_UPDATED" -> handleQrCodeUpdate(root);
                default -> log.debug("Evento ignorado: {}", event);
            }

        } catch (Exception e) {
            log.error("Erro ao processar webhook bruto", e);
        }
    }

    private void handleNewMessage(JsonNode root) {
        JsonNode data = root.path("data");
        JsonNode key = data.path("key");

        boolean fromMe = key.path("fromMe").asBoolean(false);
        if (fromMe) return;

        // Lógica robusta para extrair o número do cliente
        String senderJid = extractRealSenderJid(root, data, key);
        // Número da instância fixo (ajuste conforme necessário)
        String instanceNumber = "5511949791718";
        // Se for @lid, priorizar senderPn ou remoteJidAlt
        if (senderJid != null && senderJid.endsWith("@lid")) {
            String senderPn = root.has("senderPn") ? root.path("senderPn").asText(null) : null;
            String remoteJidAlt = root.has("remoteJidAlt") ? root.path("remoteJidAlt").asText(null) : null;
            // Preferência: senderPn > remoteJidAlt > sender
            if (senderPn != null && senderPn.endsWith("@s.whatsapp.net")) {
                senderJid = senderPn;
            } else if (remoteJidAlt != null && remoteJidAlt.endsWith("@s.whatsapp.net")) {
                senderJid = remoteJidAlt;
            } else {
                String senderFromPayload = root.has("sender") ? root.path("sender").asText(null) : null;
                // Só usa sender se não for o número da instância
                if (senderFromPayload != null && senderFromPayload.endsWith("@s.whatsapp.net")) {
                    // Extrai apenas o número do senderFromPayload para comparar
                    String senderNumberOnly = normalizeNumber(senderFromPayload);
                    if (!senderNumberOnly.equals(instanceNumber)) {
                        senderJid = senderFromPayload;
                    }
                }
            }
        }
        String messageText = extractMessageText(data);

        if (senderJid == null || messageText == null || messageText.isBlank()) return;

        String senderNumber = normalizeNumber(senderJid);

        log.info("📩 Mensagem recebida de {}: {}", senderNumber, messageText);

        messageHandler.handleIncomingMessage(senderNumber, null, messageText, root);
    }

    private String extractSenderNumber(String remoteJid) {
        if (remoteJid == null) return null;
        return remoteJid.replace("@s.whatsapp.net", "");
    }

    private String extractMessageText(JsonNode data) {
        JsonNode msg = data.path("message");

        if (msg.has("conversation")) return msg.path("conversation").asText();

        if (msg.has("extendedTextMessage"))
            return msg.path("extendedTextMessage").path("text").asText();

        return null;
    }

    private void handleMessageUpdate(JsonNode root) {
        log.debug("Mensagem atualizada");
    }

    private void handleConnectionUpdate(JsonNode root) {
        log.info("🔌 Conexão atualizada");
    }

    private void handleQrCodeUpdate(JsonNode root) {
        log.info("📱 QRCode atualizado");
    }

    private String extractRealSenderJid(JsonNode root, JsonNode data, JsonNode key) {

        if (key.has("remoteJid")) {
            return key.path("remoteJid").asText(null);
        }
        if (key.has("participant")) {
            return key.path("participant").asText(null);
        }
        if (data.has("participant")) {
            return data.path("participant").asText(null);
        }
        if (root.has("sender")) {
            String sender = root.path("sender").asText(null);
            if (sender != null && sender.contains("@s.whatsapp.net")) {
                return sender;
            }
        }
        return null;
    }
    private String normalizeNumber(String jid) {
        if (jid == null) return null;
        return jid.replace("@s.whatsapp.net", "").replace("@c.us", "");
    }

}

