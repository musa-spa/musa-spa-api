package spa.musa.send.mensage.webhook;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller que recebe os webhooks da Evolution API
 *
 * A Evolution API envia eventos para este endpoint quando:
 * - Mensagem é recebida (MESSAGES_UPSERT)
 * - Mensagem é atualizada/lida (MESSAGES_UPDATE)
 * - Status da conexão muda (CONNECTION_UPDATE)
 * - QR Code é gerado/atualizado (QRCODE_UPDATED)
 */
@AllArgsConstructor
@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final WebhookProcessor webhookProcessor;

    @GetMapping("/whatsapp")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Webhook online 🚀");
    }

    @PostMapping("/whatsapp")
    public ResponseEntity<Void> handleWebhook(@RequestBody String rawPayload) {

        log.debug("📥 Payload bruto recebido: {}", rawPayload);

        try {
            webhookProcessor.process(rawPayload);
        } catch (Exception e) {
            log.error("Erro ao processar webhook", e);
            // NUNCA devolva erro → evita reenvio infinito da Evolution
        }

        return ResponseEntity.ok().build();
    }

}