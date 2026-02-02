package spa.musa.send.mensage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spa.musa.send.mensage.dto.LoginRequest;
import spa.musa.send.mensage.dto.LoginResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints de autenticação")
public class AuthController {

    // Usuário e senha mockados para demonstração
    // Em produção, isso deve ser substituído por autenticação real (JWT, OAuth2, etc.)
    private static final String MOCK_EMAIL = "admin@musasp.com.br";
    private static final String MOCK_PASSWORD = "admin123";

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica um usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // Validação mockada - substituir por autenticação real
        if (MOCK_EMAIL.equals(request.getEmail()) && MOCK_PASSWORD.equals(request.getPassword())) {
            return ResponseEntity.ok(LoginResponse.builder()
                    .success(true)
                    .message("Login realizado com sucesso!")
                    .userName("Administrador")
                    .token("mock-jwt-token-" + System.currentTimeMillis())
                    .build());
        }

        return ResponseEntity.status(401).body(LoginResponse.builder()
                .success(false)
                .message("E-mail ou senha incorretos")
                .build());
    }

    @PostMapping("/request-access")
    @Operation(summary = "Solicitar acesso", description = "Envia uma solicitação de acesso ao sistema")
    @ApiResponse(responseCode = "200", description = "Solicitação enviada com sucesso")
    public ResponseEntity<Map<String, String>> requestAccess(@RequestBody Map<String, String> request) {
        // Em produção, isso deve salvar a solicitação no banco e notificar os administradores
        return ResponseEntity.ok(Map.of(
                "message", "Solicitação enviada com sucesso. Um administrador irá analisar em breve."
        ));
    }

    @GetMapping("/me")
    @Operation(summary = "Dados do usuário logado", description = "Retorna os dados do usuário autenticado")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        // Em produção, isso deve retornar os dados do usuário baseado no token JWT
        return ResponseEntity.ok(Map.of(
                "id", 1,
                "name", "Administrador",
                "email", MOCK_EMAIL,
                "role", "ADMIN"
        ));
    }
}
