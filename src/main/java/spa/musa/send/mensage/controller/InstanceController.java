package spa.musa.send.mensage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spa.musa.send.mensage.config.EvolutionConfig;
import spa.musa.send.mensage.evolution.EvolutionClient;

import java.util.Map;

/**
 * Controller para gerenciar instâncias e conexão WhatsApp
 */
@RestController
@RequestMapping("/api/instance")
public class InstanceController {

    private final EvolutionClient evolutionClient;
    private final EvolutionConfig evolutionConfig;

    public InstanceController(EvolutionClient evolutionClient, EvolutionConfig evolutionConfig) {
        this.evolutionClient = evolutionClient;
        this.evolutionConfig = evolutionConfig;
    }

    /**
     * Verifica status da conexão
     * GET /api/instance/status
     */
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        String status = evolutionClient.getConnectionState();
        return ResponseEntity.ok(status);
    }

    /**
     * Obtém QR Code para conexão
     * GET /api/instance/qrcode
     */
    @GetMapping("/qrcode")
    public ResponseEntity<String> getQrCode() {
        String qrcode = evolutionClient.getQrCode(evolutionConfig.getInstance().getName());
        return ResponseEntity.ok(qrcode);
    }

    /**
     * Cria nova instância com webhook configurado
     * POST /api/instance/create
     * {
     *   "instanceName": "minha-instancia",
     *   "webhookUrl": "https://meuservidor.com/webhook/whatsapp"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<String> createInstance(@RequestBody CreateInstanceRequest request) {
        String result = evolutionClient.createInstance(request.instanceName(), request.webhookUrl());
        return ResponseEntity.ok(result);
    }

    /**
     * Configura webhook para instância existente
     * POST /api/instance/webhook
     * {
     *   "webhookUrl": "https://meuservidor.com/webhook/whatsapp"
     * }
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> setWebhook(@RequestBody SetWebhookRequest request) {
        String result = evolutionClient.setWebhook(
            evolutionConfig.getInstance().getName(), 
            request.webhookUrl()
        );
        return ResponseEntity.ok(result);
    }

    public record CreateInstanceRequest(String instanceName, String webhookUrl) {}
    public record SetWebhookRequest(String webhookUrl) {}
}
