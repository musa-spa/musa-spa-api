package spa.musa.send.mensage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Sistema", description = "Endpoints do sistema")
public class SystemController {

    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Verifica se a API está funcionando")
    @ApiResponse(responseCode = "200", description = "API está funcionando")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "service", "Musa SPA API"
        ));
    }

    @GetMapping("/categories")
    @Operation(summary = "Listar categorias", description = "Retorna a lista de categorias de serviços disponíveis")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(List.of(
                "Facial",
                "Corporal",
                "Massagem",
                "Depilação",
                "Estética",
                "Bem-estar"
        ));
    }

    @GetMapping("/rooms")
    @Operation(summary = "Listar salas", description = "Retorna a lista de salas disponíveis para agendamento")
    public ResponseEntity<List<String>> getRooms() {
        return ResponseEntity.ok(List.of(
                "Sala 1",
                "Sala 2"
        ));
    }

    @GetMapping("/time-slots")
    @Operation(summary = "Listar horários", description = "Retorna a lista de horários disponíveis para agendamento")
    public ResponseEntity<List<String>> getTimeSlots() {
        return ResponseEntity.ok(List.of(
                "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00"
        ));
    }

    @GetMapping("/target-audiences")
    @Operation(summary = "Listar públicos-alvo", description = "Retorna a lista de públicos-alvo para campanhas")
    public ResponseEntity<List<Map<String, String>>> getTargetAudiences() {
        return ResponseEntity.ok(List.of(
                Map.of("value", "all", "label", "Todas as clientes"),
                Map.of("value", "active", "label", "Clientes ativas"),
                Map.of("value", "inactive30", "label", "Sem visitas há 30 dias"),
                Map.of("value", "inactive60", "label", "Sem visitas há 60 dias"),
                Map.of("value", "vip", "label", "Clientes VIP")
        ));
    }
}
