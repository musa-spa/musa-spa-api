package spa.musa.send.mensage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spa.musa.send.mensage.dto.WhatsAppContactResponse;
import spa.musa.send.mensage.dto.WhatsAppKpisResponse;
import spa.musa.send.mensage.dto.WhatsAppStatusResponse;
import spa.musa.send.mensage.service.WhatsAppService;

import java.util.List;

@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
@Tag(name = "WhatsApp", description = "Gerenciamento da integração com WhatsApp")
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    @GetMapping("/kpis")
    @Operation(summary = "Obter KPIs do WhatsApp", description = "Retorna os indicadores de performance do WhatsApp")
    @ApiResponse(responseCode = "200", description = "KPIs retornados com sucesso")
    public ResponseEntity<WhatsAppKpisResponse> getKpis() {
        return ResponseEntity.ok(whatsAppService.getKpis());
    }

    @GetMapping("/contacts")
    @Operation(summary = "Listar contatos ativos", description = "Retorna a lista de contatos com conversas ativas")
    @ApiResponse(responseCode = "200", description = "Contatos retornados com sucesso")
    public ResponseEntity<List<WhatsAppContactResponse>> getActiveContacts() {
        return ResponseEntity.ok(whatsAppService.getActiveContacts());
    }

    @GetMapping("/contacts/all")
    @Operation(summary = "Listar todos os contatos", description = "Retorna a lista completa de contatos")
    @ApiResponse(responseCode = "200", description = "Contatos retornados com sucesso")
    public ResponseEntity<List<WhatsAppContactResponse>> getAllContacts() {
        return ResponseEntity.ok(whatsAppService.getAllContacts());
    }

    @GetMapping("/status")
    @Operation(summary = "Verificar status da integração", description = "Retorna o status de conexão com a API Evolution")
    @ApiResponse(responseCode = "200", description = "Status retornado com sucesso")
    public ResponseEntity<WhatsAppStatusResponse> getStatus() {
        return ResponseEntity.ok(whatsAppService.getStatus());
    }
}
