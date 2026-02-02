package spa.musa.send.mensage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de configuração")
public class ConfigurationRequest {

    @Schema(description = "Configurações gerais")
    private GeneralSettings general;

    @Schema(description = "Configurações de notificações")
    private NotificationSettings notifications;

    @Schema(description = "Configurações do WhatsApp")
    private WhatsAppSettings whatsapp;

    @Schema(description = "Configurações de automação")
    private AutomationSettings automation;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GeneralSettings {
        private String clinicName;
        private String phone;
        private String email;
        private String address;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationSettings {
        private Boolean newAppointment;
        private Boolean clientConfirmation;
        private Boolean humanAttendance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WhatsAppSettings {
        private String evolutionApiUrl;
        private String apiKey;
        private String instance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AutomationSettings {
        private Boolean appointmentReminder;
        private Boolean autoConfirmation;
        private Boolean postServiceMessage;
    }
}
