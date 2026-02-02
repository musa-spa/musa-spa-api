package spa.musa.send.mensage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spa.musa.send.mensage.dto.ClientRequest;
import spa.musa.send.mensage.dto.ClientResponse;
import spa.musa.send.mensage.dto.ServiceHistoryResponse;
import spa.musa.send.mensage.entity.Client;
import spa.musa.send.mensage.entity.ServiceHistory;
import spa.musa.send.mensage.repository.ClientRepository;
import spa.musa.send.mensage.repository.ServiceHistoryRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ServiceHistoryRepository serviceHistoryRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<ClientResponse> findAll() {
        return clientRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ClientResponse findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return toResponse(client);
    }

    public List<ClientResponse> search(String term) {
        return clientRepository.findByNameContainingIgnoreCaseOrPhoneContainingOrEmailContainingIgnoreCase(term, term, term)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClientResponse create(ClientRequest request) {
        Client client = Client.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .notes(request.getNotes())
                .build();

        return toResponse(clientRepository.save(client));
    }

    @Transactional
    public ClientResponse update(Long id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        client.setName(request.getName());
        client.setPhone(request.getPhone());
        client.setEmail(request.getEmail());
        client.setNotes(request.getNotes());

        return toResponse(clientRepository.save(client));
    }

    @Transactional
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado");
        }
        clientRepository.deleteById(id);
    }

    public Long count() {
        return clientRepository.count();
    }

    public Long countNewThisMonth() {
        return clientRepository.countNewClientsThisMonth();
    }

    private ClientResponse toResponse(Client client) {
        List<ServiceHistory> history = serviceHistoryRepository.findByClientIdOrderByServiceDateDesc(client.getId());

        List<ServiceHistoryResponse> historyResponse = history.stream()
                .map(h -> ServiceHistoryResponse.builder()
                        .id(h.getId())
                        .date(h.getServiceDate().format(DATE_FORMAT))
                        .service(h.getService().getName())
                        .price(h.getService().getPrice())
                        .status(h.getStatus().name().toLowerCase())
                        .build())
                .collect(Collectors.toList());

        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .phone(client.getPhone())
                .email(client.getEmail())
                .notes(client.getNotes())
                .lastVisit(client.getLastVisit() != null ? client.getLastVisit().format(DATE_FORMAT) : null)
                .totalVisits(client.getTotalVisits())
                .createdAt(client.getCreatedAt() != null ? client.getCreatedAt().format(DATE_FORMAT) : null)
                .serviceHistory(historyResponse)
                .build();
    }
}
