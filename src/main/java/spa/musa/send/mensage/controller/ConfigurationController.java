package spa.musa.send.mensage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spa.musa.send.mensage.dto.ConfigurationRequest;
import spa.musa.send.mensage.service.ConfigurationService;

import java.util.Map;

@RestController
@RequestMapping("/api/configurations")
@RequiredArgsConstructor
@Tag(name = "Configurações", description = "Gerenciamento das configurações do sistema")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping
    @Operation(summary = "Obter todas as configurações", description = "Retorna todas as configurações do sistema agrupadas")
    @ApiResponse(responseCode = "200", description = "Configurações retornadas com sucesso")
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(configurationService.getAllConfigurations());
    }

    @PostMapping
    @Operation(summary = "Salvar configurações", description = "Salva todas as configurações do sistema")
    @ApiResponse(responseCode = "200", description = "Configurações salvas com sucesso")
    public ResponseEntity<Map<String, String>> saveAll(@Valid @RequestBody ConfigurationRequest request) {
        configurationService.saveAll(request);
        return ResponseEntity.ok(Map.of("message", "Configurações salvas com sucesso"));
    }
}
