package spa.musa.send.mensage.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import spa.musa.send.mensage.evolution.EvolutionClient;

import java.util.List;

/**
 * Serviço para envio de campanhas/promoções em massa
 * 
 * USE COM RESPONSABILIDADE! WhatsApp pode banir números que fazem spam.
 * Recomendações:
 * - Envie apenas para quem autorizou
 * - Respeite intervalos entre mensagens
 * - Limite quantidade diária
 */
@Service
@EnableAsync
public class CampaignService {

    private static final Logger log = LoggerFactory.getLogger(CampaignService.class);

    private final EvolutionClient evolutionClient;

    // Delay entre mensagens (em ms) - IMPORTANTE para não ser banido
    private static final int DELAY_BETWEEN_MESSAGES = 5000; // 5 segundos

    public CampaignService(EvolutionClient evolutionClient) {
        this.evolutionClient = evolutionClient;
    }

    /**
     * Envia promoção de texto para lista de números
     */
    @Async
    public void sendPromotionText(List<String> numbers, String message) {
        log.info("Iniciando campanha de texto para {} contatos", numbers.size());
        
        int sent = 0;
        int failed = 0;

        for (String number : numbers) {
            try {
                evolutionClient.sendText(number, message);
                sent++;
                log.debug("✅ Enviado para: {}", number);
                
                // Delay para evitar bloqueio
                Thread.sleep(DELAY_BETWEEN_MESSAGES);
                
            } catch (Exception e) {
                failed++;
                log.error("❌ Falha ao enviar para {}: {}", number, e.getMessage());
            }
        }

        log.info("Campanha finalizada: {} enviados, {} falhas", sent, failed);
    }

    /**
     * Envia promoção com imagem para lista de números
     */
    @Async
    public void sendPromotionImage(List<String> numbers, String imageUrl, String caption) {
        log.info("Iniciando campanha de imagem para {} contatos", numbers.size());

        int sent = 0;
        int failed = 0;

        for (String number : numbers) {
            try {
                evolutionClient.sendImage(number, imageUrl, caption);
                sent++;
                log.debug("✅ Enviado para: {}", number);
                
                Thread.sleep(DELAY_BETWEEN_MESSAGES);
                
            } catch (Exception e) {
                failed++;
                log.error("❌ Falha ao enviar para {}: {}", number, e.getMessage());
            }
        }

        log.info("Campanha de imagem finalizada: {} enviados, {} falhas", sent, failed);
    }

    /**
     * Envia mensagem individual (útil para notificações, lembretes)
     */
    public void sendNotification(String number, String message) {
        try {
            evolutionClient.sendText(number, message);
            log.info("Notificação enviada para: {}", number);
        } catch (Exception e) {
            log.error("Erro ao enviar notificação para {}: {}", number, e.getMessage());
            throw e;
        }
    }

    /**
     * Envia lembrete de agendamento
     */
    public void sendAppointmentReminder(String number, String clientName, String date, String time) {
        String message = String.format("""
            🔔 *Lembrete de Agendamento*
            
            Olá, %s!
            
            Lembramos que você tem um compromisso agendado:
            📅 Data: %s
            ⏰ Horário: %s
            
            Confirme sua presença respondendo:
            *SIM* - Confirmo presença
            *NAO* - Preciso remarcar
            
            Obrigado!
            """, clientName, date, time);

        sendNotification(number, message);
    }

    /**
     * Envia confirmação de pedido
     */
    public void sendOrderConfirmation(String number, String orderNumber, String total) {
        String message = String.format("""
            ✅ *Pedido Confirmado!*
            
            Seu pedido #%s foi recebido com sucesso!
            
            💰 Total: R$ %s
            
            Você receberá atualizações sobre o status da entrega.
            
            Obrigado pela preferência! 🙏
            """, orderNumber, total);

        sendNotification(number, message);
    }
}
