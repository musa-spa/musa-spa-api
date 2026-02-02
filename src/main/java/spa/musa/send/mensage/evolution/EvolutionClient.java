package spa.musa.send.mensage.evolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import spa.musa.send.mensage.config.EvolutionConfig;
import spa.musa.send.mensage.evolution.dto.*;

/**
 * Cliente HTTP para comunicação com a Evolution API
 *
 * Este serviço encapsula todas as chamadas à Evolution API
 */
@Service
public class EvolutionClient {

    private static final Logger log = LoggerFactory.getLogger(EvolutionClient.class);

    private final RestClient restClient;
    private final EvolutionConfig config;

    public EvolutionClient(RestClient restClient, EvolutionConfig config) {
        this.restClient = restClient;
        this.config = config;
    }

    public String sendText(String number, String text) {
        return sendText(config.getInstance().getName(), number, text);
    }

    public String sendText(String instance, String number, String text) {
        String remoteJid = number;
        if (!remoteJid.endsWith("@s.whatsapp.net")) {
            remoteJid = remoteJid.replaceAll("[^0-9]", "");
        }
        String numberParam = remoteJid;
        if (!numberParam.matches("^\\d+$")) {
            numberParam = numberParam.replaceAll("[^0-9]", "");
        }
        log.info("Enviando texto para {} via instância {} (remoteJid: {})", numberParam, instance, remoteJid + "@s.whatsapp.net");

        var request = new spa.musa.send.mensage.evolution.dto.SendTextRequest(numberParam, text);
        String url = config.getApi().getUrl() + "/message/sendText/" + instance;

        int maxRetries = 3;
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return restClient.post()
                    .uri(url)
                    .header("apikey", config.getApi().getKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);
            } catch (Exception e) {
                lastException = e;
                log.warn("Tentativa {} de {} falhou: {}", attempt, maxRetries, e.getMessage());
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(500 * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        log.error("Falha ao enviar mensagem após {} tentativas", maxRetries, lastException);
        throw new RuntimeException("Erro ao enviar mensagem: " + (lastException != null ? lastException.getMessage() : "unknown"));
    }

    /**
     * Envia imagem com legenda
     */
    public String sendImage(String number, String imageUrl, String caption) {
        return sendMedia(config.getInstance().getName(),
            SendMediaRequest.image(formatNumber(number), imageUrl, caption));
    }

    /**
     * Envia documento/arquivo
     */
    public String sendDocument(String number, String documentUrl, String fileName) {
        return sendMedia(config.getInstance().getName(),
            SendMediaRequest.document(formatNumber(number), documentUrl, fileName));
    }

    /**
     * Envia vídeo
     */
    public String sendVideo(String number, String videoUrl, String caption) {
        return sendMedia(config.getInstance().getName(),
            SendMediaRequest.video(formatNumber(number), videoUrl, caption));
    }

    private String sendMedia(String instance, SendMediaRequest request) {
        log.info("Enviando mídia {} para {}", request.mediaType(), request.number());

        return restClient.post()
            .uri(config.getApi().getUrl() + "/message/sendMedia/" + instance)
            .header("apikey", config.getApi().getKey())
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(String.class);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // MENSAGENS INTERATIVAS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Envia mensagem com botões de resposta rápida
     */
    public String sendButtons(String number, String title, String description,
                             java.util.List<SendButtonsRequest.Button> buttons) {
        log.info("Enviando botões para {}", number);

        var request = SendButtonsRequest.create(formatNumber(number), title, description, buttons);

        return restClient.post()
            .uri(config.getApi().getUrl() + "/message/sendButtons/" + config.getInstance().getName())
            .header("apikey", config.getApi().getKey())
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(String.class);
    }

    /**
     * Envia lista/menu de opções
     */
    public String sendList(String number, String title, String description,
                          String buttonText, java.util.List<SendListRequest.Section> sections) {
        log.info("Enviando lista para {}", number);

        var request = SendListRequest.create(formatNumber(number), title, description, buttonText, sections);

        return restClient.post()
            .uri(config.getApi().getUrl() + "/message/sendList/" + config.getInstance().getName())
            .header("apikey", config.getApi().getKey())
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(String.class);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GERENCIAMENTO DE INSTÂNCIAS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Verifica status da conexão da instância
     */
    public String getConnectionState() {
        return getConnectionState(config.getInstance().getName());
    }

    public String getConnectionState(String instance) {
        return restClient.get()
            .uri(config.getApi().getUrl() + "/instance/connectionState/" + instance)
            .header("apikey", config.getApi().getKey())
            .retrieve()
            .body(String.class);
    }

    /**
     * Obtém QR Code para conexão
     */
    public String getQrCode(String instance) {
        return restClient.get()
            .uri(config.getApi().getUrl() + "/instance/connect/" + instance)
            .header("apikey", config.getApi().getKey())
            .retrieve()
            .body(String.class);
    }

    /**
     * Cria nova instância
     */
    public String createInstance(String instanceName, String webhookUrl) {
        log.info("Criando instância: {}", instanceName);

        var body = java.util.Map.of(
            "instanceName", instanceName,
            "integration", "WHATSAPP-BAILEYS",
            "webhook", java.util.Map.of(
                "url", webhookUrl,
                "enabled", true,
                "webhookByEvents", false,
                "events", java.util.List.of(
                    "MESSAGES_UPSERT",
                    "MESSAGES_UPDATE",
                    "CONNECTION_UPDATE",
                    "QRCODE_UPDATED"
                )
            )
        );

        return restClient.post()
            .uri(config.getApi().getUrl() + "/instance/create")
            .header("apikey", config.getApi().getKey())
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .retrieve()
            .body(String.class);
    }

    /**
     * Configura webhook para uma instância existente
     */
    public String setWebhook(String instance, String webhookUrl) {
        log.info("Configurando webhook para instância {}: {}", instance, webhookUrl);

        // A Evolution API espera o objeto webhook dentro de "webhook"
        var webhookConfig = java.util.Map.of(
            "enabled", true,
            "url", webhookUrl,
            "webhookByEvents", false,
            "events", java.util.List.of(
                "MESSAGES_UPSERT",
                "MESSAGES_UPDATE",
                "CONNECTION_UPDATE",
                "QRCODE_UPDATED"
            )
        );

        var body = java.util.Map.of("webhook", webhookConfig);

        return restClient.post()
            .uri(config.getApi().getUrl() + "/webhook/set/" + instance)
            .header("apikey", config.getApi().getKey())
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .retrieve()
            .body(String.class);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // UTILITÁRIOS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Formata número para o padrão do WhatsApp (55XXXXXXXXXXX)
     */
    private String formatNumber(String number) {
        // Remove caracteres não numéricos
        String cleaned = number.replaceAll("[^0-9]", "");

        // Adiciona código do Brasil se não tiver
        if (!cleaned.startsWith("55")) {
            cleaned = "55" + cleaned;
        }

        return cleaned;
    }
}
