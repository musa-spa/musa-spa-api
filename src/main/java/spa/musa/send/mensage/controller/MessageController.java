package spa.musa.send.mensage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spa.musa.send.mensage.service.CampaignService;

import java.util.List;
import java.util.Map;

/**
 * API REST para disparo de mensagens e campanhas
 * 
 * Use estes endpoints para integrar com outros sistemas ou
 * criar um painel administrativo
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final CampaignService campaignService;

    public MessageController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    /**
     * Envia mensagem individual
     * 
     * POST /api/messages/send
     * {
     *   "number": "5511999999999",
     *   "message": "Olá, tudo bem?"
     * }
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody SendRequest request) {
        campaignService.sendNotification(request.number(), request.message());
        return ResponseEntity.ok(Map.of("status", "sent", "number", request.number()));
    }

    /**
     * Envia campanha para múltiplos números
     * 
     * POST /api/messages/campaign
     * {
     *   "numbers": ["5511999999999", "5511888888888"],
     *   "message": "Promoção especial!"
     * }
     */
    @PostMapping("/campaign")
    public ResponseEntity<Map<String, Object>> sendCampaign(@RequestBody CampaignRequest request) {
        campaignService.sendPromotionText(request.numbers(), request.message());
        return ResponseEntity.ok(Map.of(
            "status", "processing",
            "total", request.numbers().size(),
            "message", "Campanha iniciada em background"
        ));
    }

    /**
     * Envia campanha com imagem
     * 
     * POST /api/messages/campaign/image
     * {
     *   "numbers": ["5511999999999"],
     *   "imageUrl": "https://exemplo.com/promo.jpg",
     *   "caption": "Confira nossa promoção!"
     * }
     */
    @PostMapping("/campaign/image")
    public ResponseEntity<Map<String, Object>> sendImageCampaign(@RequestBody ImageCampaignRequest request) {
        campaignService.sendPromotionImage(request.numbers(), request.imageUrl(), request.caption());
        return ResponseEntity.ok(Map.of(
            "status", "processing",
            "total", request.numbers().size()
        ));
    }

    /**
     * Envia lembrete de agendamento
     * 
     * POST /api/messages/reminder
     * {
     *   "number": "5511999999999",
     *   "clientName": "João",
     *   "date": "25/01/2024",
     *   "time": "14:00"
     * }
     */
    @PostMapping("/reminder")
    public ResponseEntity<Map<String, String>> sendReminder(@RequestBody ReminderRequest request) {
        campaignService.sendAppointmentReminder(
            request.number(),
            request.clientName(),
            request.date(),
            request.time()
        );
        return ResponseEntity.ok(Map.of("status", "sent"));
    }

    /**
     * Envia confirmação de pedido
     * 
     * POST /api/messages/order-confirmation
     * {
     *   "number": "5511999999999",
     *   "orderNumber": "12345",
     *   "total": "299.90"
     * }
     */
    @PostMapping("/order-confirmation")
    public ResponseEntity<Map<String, String>> sendOrderConfirmation(@RequestBody OrderConfirmationRequest request) {
        campaignService.sendOrderConfirmation(request.number(), request.orderNumber(), request.total());
        return ResponseEntity.ok(Map.of("status", "sent"));
    }

    // ════════════════════════════════════════════════════════════════════════════
    // DTOs
    // ════════════════════════════════════════════════════════════════════════════

    public record SendRequest(String number, String message) {}
    public record CampaignRequest(List<String> numbers, String message) {}
    public record ImageCampaignRequest(List<String> numbers, String imageUrl, String caption) {}
    public record ReminderRequest(String number, String clientName, String date, String time) {}
    public record OrderConfirmationRequest(String number, String orderNumber, String total) {}
}
