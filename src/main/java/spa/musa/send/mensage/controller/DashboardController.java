package spa.musa.send.mensage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spa.musa.send.mensage.dto.AppointmentResponse;
import spa.musa.send.mensage.dto.DashboardStatsResponse;
import spa.musa.send.mensage.service.DashboardService;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Estatísticas e resumos do sistema")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Obter estatísticas do dashboard", description = "Retorna os principais indicadores do sistema")
    @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @GetMapping("/upcoming-appointments")
    @Operation(summary = "Próximos agendamentos", description = "Retorna os agendamentos de hoje")
    @ApiResponse(responseCode = "200", description = "Agendamentos retornados com sucesso")
    public ResponseEntity<List<AppointmentResponse>> getUpcomingAppointments() {
        return ResponseEntity.ok(dashboardService.getUpcomingAppointments());
    }
}
