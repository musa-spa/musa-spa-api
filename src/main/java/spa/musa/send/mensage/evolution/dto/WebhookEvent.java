package spa.musa.send.mensage.evolution.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Evento recebido via Webhook da Evolution API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookEvent(
        String event,
        String instance,
        Data data,
        String destination,
        @JsonProperty("date_time") String dateTime,
        String sender,
        @JsonProperty("server_url") String serverUrl,
        String apikey
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(
            Key key,
            String pushName,
            String status,
            Message message,
            @JsonProperty("messageType") String messageType,
            @JsonProperty("messageTimestamp") Long messageTimestamp,
            Owner owner,
            String source
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Key(
            String remoteJid,
            Boolean fromMe,
            String id,
            String participant  // ← CAMPO ADICIONADO para capturar o número real em listas/grupos
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(
            String conversation,           // Mensagem de texto simples
            ExtendedTextMessage extendedTextMessage,
            ImageMessage imageMessage,
            VideoMessage videoMessage,
            AudioMessage audioMessage,
            DocumentMessage documentMessage,
            ButtonsResponseMessage buttonsResponseMessage,
            ListResponseMessage listResponseMessage
    ) {
        public String getTextContent() {
            if (conversation != null) return conversation;
            if (extendedTextMessage != null) return extendedTextMessage.text();
            if (buttonsResponseMessage != null) return buttonsResponseMessage.selectedButtonId();
            if (listResponseMessage != null) return listResponseMessage.singleSelectReply().selectedRowId();
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ExtendedTextMessage(String text) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ImageMessage(String url, String mimetype, String caption) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VideoMessage(String url, String mimetype, String caption) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AudioMessage(String url, String mimetype) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DocumentMessage(String url, String mimetype, String fileName) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ButtonsResponseMessage(String selectedButtonId, String selectedDisplayText) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ListResponseMessage(SingleSelectReply singleSelectReply) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SingleSelectReply(String selectedRowId) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Owner(String id) {}

    // Métodos auxiliares
    public String getSenderNumber() {
        if (data != null && data.key() != null) {
            // PRIORIDADE 1: Tenta participant (grupos e listas Business)
            if (data.key().participant() != null && data.key().participant().contains("@s.whatsapp.net")) {
                return data.key().participant().replace("@s.whatsapp.net", "");
            }

            String jid = data.key().remoteJid();
            // Workaround Evolution API: se for @lid e sender termina com @s.whatsapp.net, usa sender como número do remetente
            if (jid != null && jid.contains("@lid") && sender != null && sender.endsWith("@s.whatsapp.net")) {
                return sender.replace("@s.whatsapp.net", "");
            }

            // PRIORIDADE 2: Tenta remoteJid (chat privado normal)
            if (jid != null && jid.contains("@s.whatsapp.net")) {
                return jid.replace("@s.whatsapp.net", "");
            }
        }

        // PRIORIDADE 3: Tenta campo 'sender' do evento raiz
        if (sender != null && sender.contains("@s.whatsapp.net")) {
            return sender.replace("@s.whatsapp.net", "");
        }

        return null;
    }

    public String getSenderName() {
        return data != null ? data.pushName() : null;
    }

    public String getMessageText() {
        if (data != null && data.message() != null) {
            return data.message().getTextContent();
        }
        return null;
    }

    public boolean isFromMe() {
        return data != null && data.key() != null && Boolean.TRUE.equals(data.key().fromMe());
    }

    public boolean isGroupMessage() {
        if (data != null && data.key() != null && data.key().remoteJid() != null) {
            return data.key().remoteJid().contains("@g.us");
        }
        return false;
    }

    /**
     * Verifica se é mensagem de canal, lista, newsletter ou broadcast
     * Esses tipos não devem receber respostas automáticas
     */
    public boolean isChannelOrBroadcast() {
        if (data != null && data.key() != null && data.key().remoteJid() != null) {
            String jid = data.key().remoteJid();
            // Workaround Evolution API: se for @lid, participant==null e sender termina com @s.whatsapp.net, trata como privado
            if (jid.contains("@lid") && (data.key().participant() == null || data.key().participant().isBlank()) && sender != null && sender.endsWith("@s.whatsapp.net")) {
                return false;
            }
            // Só retorna true se NÃO for privado
            if (jid.endsWith("@s.whatsapp.net")) {
                return false;
            }
            return jid.contains("@lid") ||
                    jid.contains("@broadcast") ||
                    jid.contains("@newsletter") ||
                    jid.contains("@channel");
        }
        return false;
    }

    /**
     * Verifica se é uma mensagem de chat privado normal (1:1)
     */
    public boolean isPrivateChat() {
        if (data != null && data.key() != null && data.key().remoteJid() != null) {
            return data.key().remoteJid().contains("@s.whatsapp.net");
        }
        return false;
    }
}