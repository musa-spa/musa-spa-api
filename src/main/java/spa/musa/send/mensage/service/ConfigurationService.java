package spa.musa.send.mensage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spa.musa.send.mensage.dto.ConfigurationRequest;
import spa.musa.send.mensage.entity.Configuration;
import spa.musa.send.mensage.repository.ConfigurationRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public Map<String, Object> getAllConfigurations() {
        List<Configuration> configs = configurationRepository.findAll();
        Map<String, Object> result = new HashMap<>();

        // Agrupa por grupo
        Map<String, Map<String, String>> grouped = new HashMap<>();
        for (Configuration config : configs) {
            String group = config.getGroup() != null ? config.getGroup() : "general";
            grouped.computeIfAbsent(group, k -> new HashMap<>())
                    .put(config.getKey(), config.getValue());
        }

        // Monta a resposta estruturada
        result.put("general", buildGeneralSettings(grouped.getOrDefault("general", new HashMap<>())));
        result.put("notifications", buildNotificationSettings(grouped.getOrDefault("notifications", new HashMap<>())));
        result.put("whatsapp", buildWhatsAppSettings(grouped.getOrDefault("whatsapp", new HashMap<>())));
        result.put("automation", buildAutomationSettings(grouped.getOrDefault("automation", new HashMap<>())));

        return result;
    }

    public String getValue(String key) {
        return configurationRepository.findByKey(key)
                .map(Configuration::getValue)
                .orElse(null);
    }

    public String getValue(String key, String defaultValue) {
        return configurationRepository.findByKey(key)
                .map(Configuration::getValue)
                .orElse(defaultValue);
    }

    @Transactional
    public void saveConfiguration(String key, String value, String group) {
        Configuration config = configurationRepository.findByKey(key)
                .orElse(Configuration.builder().key(key).group(group).build());
        config.setValue(value);
        config.setGroup(group);
        configurationRepository.save(config);
    }

    @Transactional
    public void saveAll(ConfigurationRequest request) {
        if (request.getGeneral() != null) {
            saveConfiguration("clinicName", request.getGeneral().getClinicName(), "general");
            saveConfiguration("phone", request.getGeneral().getPhone(), "general");
            saveConfiguration("email", request.getGeneral().getEmail(), "general");
            saveConfiguration("address", request.getGeneral().getAddress(), "general");
        }

        if (request.getNotifications() != null) {
            saveConfiguration("newAppointment", String.valueOf(request.getNotifications().getNewAppointment()), "notifications");
            saveConfiguration("clientConfirmation", String.valueOf(request.getNotifications().getClientConfirmation()), "notifications");
            saveConfiguration("humanAttendance", String.valueOf(request.getNotifications().getHumanAttendance()), "notifications");
        }

        if (request.getWhatsapp() != null) {
            saveConfiguration("evolutionApiUrl", request.getWhatsapp().getEvolutionApiUrl(), "whatsapp");
            saveConfiguration("apiKey", request.getWhatsapp().getApiKey(), "whatsapp");
            saveConfiguration("instance", request.getWhatsapp().getInstance(), "whatsapp");
        }

        if (request.getAutomation() != null) {
            saveConfiguration("appointmentReminder", String.valueOf(request.getAutomation().getAppointmentReminder()), "automation");
            saveConfiguration("autoConfirmation", String.valueOf(request.getAutomation().getAutoConfirmation()), "automation");
            saveConfiguration("postServiceMessage", String.valueOf(request.getAutomation().getPostServiceMessage()), "automation");
        }
    }

    private Map<String, String> buildGeneralSettings(Map<String, String> configs) {
        Map<String, String> settings = new HashMap<>();
        settings.put("clinicName", configs.getOrDefault("clinicName", "Musa SPA"));
        settings.put("phone", configs.getOrDefault("phone", "(11) 94979-1718"));
        settings.put("email", configs.getOrDefault("email", "contato@musasp.com.br"));
        settings.put("address", configs.getOrDefault("address", "Alameda Rio Negro, 1000 - Alphaville"));
        return settings;
    }

    private Map<String, Boolean> buildNotificationSettings(Map<String, String> configs) {
        Map<String, Boolean> settings = new HashMap<>();
        settings.put("newAppointment", Boolean.parseBoolean(configs.getOrDefault("newAppointment", "true")));
        settings.put("clientConfirmation", Boolean.parseBoolean(configs.getOrDefault("clientConfirmation", "true")));
        settings.put("humanAttendance", Boolean.parseBoolean(configs.getOrDefault("humanAttendance", "true")));
        return settings;
    }

    private Map<String, String> buildWhatsAppSettings(Map<String, String> configs) {
        Map<String, String> settings = new HashMap<>();
        settings.put("evolutionApiUrl", configs.getOrDefault("evolutionApiUrl", ""));
        settings.put("apiKey", configs.containsKey("apiKey") ? "••••••••" : "");
        settings.put("instance", configs.getOrDefault("instance", ""));
        return settings;
    }

    private Map<String, Boolean> buildAutomationSettings(Map<String, String> configs) {
        Map<String, Boolean> settings = new HashMap<>();
        settings.put("appointmentReminder", Boolean.parseBoolean(configs.getOrDefault("appointmentReminder", "true")));
        settings.put("autoConfirmation", Boolean.parseBoolean(configs.getOrDefault("autoConfirmation", "true")));
        settings.put("postServiceMessage", Boolean.parseBoolean(configs.getOrDefault("postServiceMessage", "false")));
        return settings;
    }
}
