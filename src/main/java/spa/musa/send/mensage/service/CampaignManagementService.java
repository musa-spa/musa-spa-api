package spa.musa.send.mensage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spa.musa.send.mensage.dto.CampaignRequest;
import spa.musa.send.mensage.dto.CampaignResponse;
import spa.musa.send.mensage.entity.Campaign;
import spa.musa.send.mensage.repository.CampaignRepository;
import spa.musa.send.mensage.repository.ClientRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignManagementService {

    private final CampaignRepository campaignRepository;
    private final ClientRepository clientRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<CampaignResponse> findAll() {
        return campaignRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CampaignResponse findById(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campanha não encontrada"));
        return toResponse(campaign);
    }

    public List<CampaignResponse> findByStatus(String status) {
        Campaign.CampaignStatus campaignStatus = Campaign.CampaignStatus.valueOf(status.toUpperCase());
        return campaignRepository.findByStatus(campaignStatus).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CampaignResponse create(CampaignRequest request) {
        Campaign.CampaignStatus status = determineStatus(request);
        int recipients = estimateRecipients(request.getTargetAudience());

        Campaign campaign = Campaign.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .targetAudience(request.getTargetAudience())
                .status(status)
                .scheduledDate(request.getScheduledDate())
                .recipients(status == Campaign.CampaignStatus.RASCUNHO ? 0 : recipients)
                .build();

        return toResponse(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignResponse update(Long id, CampaignRequest request) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campanha não encontrada"));

        campaign.setTitle(request.getTitle());
        campaign.setMessage(request.getMessage());
        campaign.setTargetAudience(request.getTargetAudience());
        campaign.setScheduledDate(request.getScheduledDate());

        if (Boolean.TRUE.equals(request.getAsDraft())) {
            campaign.setStatus(Campaign.CampaignStatus.RASCUNHO);
        } else if (request.getScheduledDate() != null) {
            campaign.setStatus(Campaign.CampaignStatus.AGENDADA);
        }

        return toResponse(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignResponse sendNow(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campanha não encontrada"));

        int recipients = estimateRecipients(campaign.getTargetAudience());

        campaign.setStatus(Campaign.CampaignStatus.ENVIADA);
        campaign.setSentDate(LocalDateTime.now());
        campaign.setRecipients(recipients);

        // TODO: Integrar com serviço de envio de mensagens WhatsApp

        return toResponse(campaignRepository.save(campaign));
    }

    @Transactional
    public void delete(Long id) {
        if (!campaignRepository.existsById(id)) {
            throw new RuntimeException("Campanha não encontrada");
        }
        campaignRepository.deleteById(id);
    }

    private Campaign.CampaignStatus determineStatus(CampaignRequest request) {
        if (Boolean.TRUE.equals(request.getAsDraft())) {
            return Campaign.CampaignStatus.RASCUNHO;
        }
        if (request.getScheduledDate() != null) {
            return Campaign.CampaignStatus.AGENDADA;
        }
        return Campaign.CampaignStatus.RASCUNHO;
    }

    private int estimateRecipients(String targetAudience) {
        long totalClients = clientRepository.count();
        return switch (targetAudience) {
            case "all" -> (int) totalClients;
            case "active" -> (int) (totalClients * 0.8);
            case "inactive30" -> (int) (totalClients * 0.15);
            case "inactive60" -> (int) (totalClients * 0.1);
            case "vip" -> (int) (totalClients * 0.2);
            default -> (int) totalClients;
        };
    }

    private CampaignResponse toResponse(Campaign campaign) {
        return CampaignResponse.builder()
                .id(campaign.getId())
                .title(campaign.getTitle())
                .message(campaign.getMessage())
                .status(campaign.getStatus().name().toLowerCase())
                .targetAudience(campaign.getTargetAudience())
                .sentDate(campaign.getSentDate() != null ? campaign.getSentDate().format(DATE_FORMAT) : null)
                .scheduledDate(campaign.getScheduledDate() != null ? campaign.getScheduledDate().format(DATE_FORMAT) : null)
                .recipients(campaign.getRecipients())
                .opens(campaign.getOpens())
                .build();
    }
}
