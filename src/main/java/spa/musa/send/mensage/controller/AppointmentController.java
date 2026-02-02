package spa.musa.send.mensage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spa.musa.send.mensage.dto.AppointmentRequest;
import spa.musa.send.mensage.dto.AppointmentResponse;
import spa.musa.send.mensage.service.AppointmentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Agendamentos", description = "Gerenciamento de agendamentos do spa")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    @Operation(summary = "Listar todos os agendamentos", description = "Retorna uma lista com todos os agendamentos")
    @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso")
    public ResponseEntity<List<AppointmentResponse>> findAll() {
        return ResponseEntity.ok(appointmentService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por ID", description = "Retorna os dados de um agendamento específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    public ResponseEntity<AppointmentResponse> findById(
            @Parameter(description = "ID do agendamento") @PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Listar agendamentos por data", description = "Retorna os agendamentos de uma data específica")
    public ResponseEntity<List<AppointmentResponse>> findByDate(
            @Parameter(description = "Data no formato YYYY-MM-DD")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.findByDate(date));
    }

    @GetMapping("/range")
    @Operation(summary = "Listar agendamentos por período", description = "Retorna os agendamentos de um período específico")
    public ResponseEntity<List<AppointmentResponse>> findByDateRange(
            @Parameter(description = "Data inicial")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Data final")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(appointmentService.findByDateRange(startDate, endDate));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Listar agendamentos do cliente", description = "Retorna os agendamentos de um cliente específico")
    public ResponseEntity<List<AppointmentResponse>> findByClient(
            @Parameter(description = "ID do cliente") @PathVariable Long clientId) {
        return ResponseEntity.ok(appointmentService.findByClient(clientId));
    }

    @PostMapping
    @Operation(summary = "Criar novo agendamento", description = "Cadastra um novo agendamento no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar agendamento", description = "Atualiza os dados de um agendamento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    public ResponseEntity<AppointmentResponse> update(
            @Parameter(description = "ID do agendamento") @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do agendamento", description = "Atualiza apenas o status de um agendamento")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @Parameter(description = "ID do agendamento") @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, body.get("status")));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir agendamento", description = "Remove um agendamento do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Agendamento excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do agendamento") @PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
