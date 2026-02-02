package spa.musa.send.mensage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spa.musa.send.mensage.dto.ServiceRequest;
import spa.musa.send.mensage.dto.ServiceResponse;
import spa.musa.send.mensage.entity.SpaService;
import spa.musa.send.mensage.repository.SpaServiceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpaServiceService {

    private final SpaServiceRepository spaServiceRepository;

    public List<ServiceResponse> findAll() {
        return spaServiceRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ServiceResponse> findActive() {
        return spaServiceRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ServiceResponse findById(Long id) {
        SpaService service = spaServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
        return toResponse(service);
    }

    public List<ServiceResponse> findByCategory(String category) {
        return spaServiceRepository.findByCategory(category).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ServiceResponse> search(String term) {
        return spaServiceRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(term, term)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServiceResponse create(ServiceRequest request) {
        SpaService service = SpaService.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .duration(request.getDuration())
                .price(request.getPrice())
                .active(request.getActive())
                .build();

        return toResponse(spaServiceRepository.save(service));
    }

    @Transactional
    public ServiceResponse update(Long id, ServiceRequest request) {
        SpaService service = spaServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setCategory(request.getCategory());
        service.setDuration(request.getDuration());
        service.setPrice(request.getPrice());
        service.setActive(request.getActive());

        return toResponse(spaServiceRepository.save(service));
    }

    @Transactional
    public void delete(Long id) {
        if (!spaServiceRepository.existsById(id)) {
            throw new RuntimeException("Serviço não encontrado");
        }
        spaServiceRepository.deleteById(id);
    }

    @Transactional
    public ServiceResponse toggleActive(Long id) {
        SpaService service = spaServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        service.setActive(!service.getActive());
        return toResponse(spaServiceRepository.save(service));
    }

    private ServiceResponse toResponse(SpaService service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .category(service.getCategory())
                .duration(service.getDuration())
                .price(service.getPrice())
                .active(service.getActive())
                .build();
    }
}
