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
import spa.musa.send.mensage.dto.ServiceRequest;
import spa.musa.send.mensage.dto.ServiceResponse;
import spa.musa.send.mensage.service.SpaServiceService;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "Serviços", description = "Gerenciamento de serviços oferecidos pelo spa")
public class ServiceController {

    private final SpaServiceService spaServiceService;

    @GetMapping
    @Operation(summary = "Listar todos os serviços", description = "Retorna uma lista com todos os serviços cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso")
    public ResponseEntity<List<ServiceResponse>> findAll() {
        return ResponseEntity.ok(spaServiceService.findAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Listar serviços ativos", description = "Retorna apenas os serviços que estão ativos")
    public ResponseEntity<List<ServiceResponse>> findActive() {
        return ResponseEntity.ok(spaServiceService.findActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID", description = "Retorna os dados de um serviço específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServiceResponse> findById(
            @Parameter(description = "ID do serviço") @PathVariable Long id) {
        return ResponseEntity.ok(spaServiceService.findById(id));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Listar serviços por categoria", description = "Retorna os serviços de uma categoria específica")
    public ResponseEntity<List<ServiceResponse>> findByCategory(
            @Parameter(description = "Nome da categoria") @PathVariable String category) {
        return ResponseEntity.ok(spaServiceService.findByCategory(category));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar serviços", description = "Busca serviços por nome ou descrição")
    public ResponseEntity<List<ServiceResponse>> search(
            @Parameter(description = "Termo de busca") @RequestParam String term) {
        return ResponseEntity.ok(spaServiceService.search(term));
    }

    @PostMapping
    @Operation(summary = "Criar novo serviço", description = "Cadastra um novo serviço no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ServiceResponse> create(@Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(spaServiceService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço", description = "Atualiza os dados de um serviço existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServiceResponse> update(
            @Parameter(description = "ID do serviço") @PathVariable Long id,
            @Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(spaServiceService.update(id, request));
    }

    @PatchMapping("/{id}/toggle-active")
    @Operation(summary = "Alternar status ativo", description = "Ativa ou desativa um serviço")
    public ResponseEntity<ServiceResponse> toggleActive(
            @Parameter(description = "ID do serviço") @PathVariable Long id) {
        return ResponseEntity.ok(spaServiceService.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir serviço", description = "Remove um serviço do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do serviço") @PathVariable Long id) {
        spaServiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
