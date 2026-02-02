package spa.musa.send.mensage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spa.musa.send.mensage.dto.CampaignRequest;
import spa.musa.send.mensage.dto.CampaignResponse;
import spa.musa.send.mensage.service.CampaignManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
@Tag(name = "Campanhas", description = "Gerenciamento de campanhas de marketing")
public class CampaignController {

    private final CampaignManagementService campaignManagementService;

    @GetMapping
    @Operation(summary = "Listar todas as campanhas", description = "Retorna uma lista com todas as campanhas cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de campanhas retornada com sucesso")
    public ResponseEntity<List<CampaignResponse>> findAll() {
        return ResponseEntity.ok(campaignManagementService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar campanha por ID", description = "Retorna os dados de uma campanha específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campanha encontrada"),
            @ApiResponse(responseCode = "404", description = "Campanha não encontrada")
    })
    public ResponseEntity<CampaignResponse> findById(
            @Parameter(description = "ID da campanha") @PathVariable Long id) {
        return ResponseEntity.ok(campaignManagementService.findById(id));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar campanhas por status", description = "Retorna as campanhas com um status específico")
    public ResponseEntity<List<CampaignResponse>> findByStatus(
            @Parameter(description = "Status da campanha (rascunho, agendada, enviada)") @PathVariable String status) {
        return ResponseEntity.ok(campaignManagementService.findByStatus(status));
    }

    @PostMapping
    @Operation(summary = "Criar nova campanha", description = "Cadastra uma nova campanha no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Campanha criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<CampaignResponse> create(@Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campaignManagementService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar campanha", description = "Atualiza os dados de uma campanha existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campanha atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Campanha não encontrada")
    })
    public ResponseEntity<CampaignResponse> update(
            @Parameter(description = "ID da campanha") @PathVariable Long id,
            @Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.ok(campaignManagementService.update(id, request));
    }

    @PostMapping("/{id}/send")
    @Operation(summary = "Enviar campanha agora", description = "Envia uma campanha imediatamente para os destinatários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campanha enviada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Campanha não encontrada")
    })
    public ResponseEntity<CampaignResponse> sendNow(
            @Parameter(description = "ID da campanha") @PathVariable Long id) {
        return ResponseEntity.ok(campaignManagementService.sendNow(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir campanha", description = "Remove uma campanha do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Campanha excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Campanha não encontrada")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da campanha") @PathVariable Long id) {
        campaignManagementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
