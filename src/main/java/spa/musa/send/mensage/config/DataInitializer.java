package spa.musa.send.mensage.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import spa.musa.send.mensage.entity.*;
import spa.musa.send.mensage.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final ClientRepository clientRepository;
    private final SpaServiceRepository spaServiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final CampaignRepository campaignRepository;
    private final WhatsAppContactRepository whatsAppContactRepository;
    private final ConfigurationRepository configurationRepository;

    @Bean
    @Profile("!test")
    CommandLineRunner initDatabase() {
        return args -> {
            if (spaServiceRepository.count() == 0) {
                log.info("Inicializando dados de exemplo...");
                initServices();
                initClients();
                initAppointments();
                initCampaigns();
                initWhatsAppContacts();
                initConfigurations();
                log.info("Dados de exemplo inicializados com sucesso!");
            }
        };
    }

    private void initServices() {
        spaServiceRepository.save(createService("Limpeza de Pele", "Limpeza profunda da pele com extração de cravos e hidratação", "Facial", 60, "150.00", true));
        spaServiceRepository.save(createService("Detox Facial", "Tratamento desintoxicante para pele oleosa e com acne", "Facial", 75, "180.00", true));
        spaServiceRepository.save(createService("Massagem Relaxante", "Massagem corporal com óleos essenciais para relaxamento total", "Massagem", 60, "200.00", true));
        spaServiceRepository.save(createService("Massagem Modeladora", "Massagem intensiva para redução de medidas e celulite", "Massagem", 50, "180.00", true));
        spaServiceRepository.save(createService("Drenagem Linfática", "Técnica para eliminar toxinas e reduzir inchaço", "Corporal", 60, "170.00", true));
        spaServiceRepository.save(createService("Peeling Corporal", "Esfoliação profunda para renovação celular", "Corporal", 45, "120.00", false));
        spaServiceRepository.save(createService("Hidratação Facial", "Tratamento intensivo de hidratação para peles secas", "Facial", 45, "130.00", true));
        spaServiceRepository.save(createService("Day Spa Completo", "Pacote completo com massagem, facial e relaxamento", "Bem-estar", 180, "450.00", true));
    }

    private SpaService createService(String name, String description, String category, int duration, String price, boolean active) {
        SpaService service = new SpaService();
        service.setName(name);
        service.setDescription(description);
        service.setCategory(category);
        service.setDuration(duration);
        service.setPrice(new BigDecimal(price));
        service.setActive(active);
        return service;
    }

    private void initClients() {
        clientRepository.save(createClient("Maria Silva", "(11) 99999-1111", "maria@email.com", "Cliente VIP, prefere horários pela manhã", 12, 2));
        clientRepository.save(createClient("Ana Costa", "(11) 99999-2222", "ana@email.com", "", 8, 4));
        clientRepository.save(createClient("Juliana Santos", "(11) 99999-3333", "juliana@email.com", "Alergia a produtos com parabenos", 5, 7));
        clientRepository.save(createClient("Carla Oliveira", "(11) 99999-4444", "carla@email.com", "", 15, 12));
        clientRepository.save(createClient("Patricia Lima", "(11) 99999-5555", "patricia@email.com", "Nova cliente, indicação da Maria Silva", 3, 17));
    }

    private Client createClient(String name, String phone, String email, String notes, int totalVisits, int daysAgo) {
        Client client = new Client();
        client.setName(name);
        client.setPhone(phone);
        client.setEmail(email);
        client.setNotes(notes);
        client.setTotalVisits(totalVisits);
        client.setLastVisit(LocalDateTime.now().minusDays(daysAgo));
        return client;
    }

    private void initAppointments() {
        var clients = clientRepository.findAll();
        var services = spaServiceRepository.findAll();

        if (!clients.isEmpty() && !services.isEmpty()) {
            LocalDate today = LocalDate.now();
            appointmentRepository.save(createAppointment(clients.get(0), services.get(0), today, LocalTime.of(9, 0), "Sala 1", Appointment.AppointmentStatus.CONFIRMADO));
            appointmentRepository.save(createAppointment(clients.get(1), services.get(1), today, LocalTime.of(10, 30), "Sala 2", Appointment.AppointmentStatus.PENDENTE));
            appointmentRepository.save(createAppointment(clients.get(2), services.get(2), today, LocalTime.of(11, 0), "Sala 1", Appointment.AppointmentStatus.CONFIRMADO));
            appointmentRepository.save(createAppointment(clients.get(3), services.get(0), today, LocalTime.of(14, 0), "Sala 2", Appointment.AppointmentStatus.CONFIRMADO));
            appointmentRepository.save(createAppointment(clients.get(4), services.get(3), today, LocalTime.of(15, 30), "Sala 1", Appointment.AppointmentStatus.PENDENTE));
            appointmentRepository.save(createAppointment(clients.get(0), services.get(2), today.plusDays(1), LocalTime.of(10, 0), "Sala 1", Appointment.AppointmentStatus.CONFIRMADO));
            appointmentRepository.save(createAppointment(clients.get(1), services.get(4), today.plusDays(2), LocalTime.of(14, 30), "Sala 2", Appointment.AppointmentStatus.PENDENTE));
        }
    }

    private Appointment createAppointment(Client client, SpaService service, LocalDate date, LocalTime time, String room, Appointment.AppointmentStatus status) {
        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setService(service);
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setRoom(room);
        appointment.setStatus(status);
        return appointment;
    }

    private void initCampaigns() {
        campaignRepository.save(createCampaign("Promoção de Verão", "Aproveite 20% de desconto em todos os tratamentos faciais!", Campaign.CampaignStatus.ENVIADA, "all", LocalDateTime.now().minusDays(2), null, 45, 38));
        campaignRepository.save(createCampaign("Lançamento Detox", "Conheça nosso novo tratamento de Detox Facial Premium!", Campaign.CampaignStatus.AGENDADA, "active", null, LocalDateTime.now().plusDays(3), 120, 0));
        campaignRepository.save(createCampaign("Aniversário Musa", "Comemore conosco! Condições especiais para você.", Campaign.CampaignStatus.RASCUNHO, "vip", null, null, 0, 0));
    }

    private Campaign createCampaign(String title, String message, Campaign.CampaignStatus status, String targetAudience, LocalDateTime sentDate, LocalDateTime scheduledDate, int recipients, int opens) {
        Campaign campaign = new Campaign();
        campaign.setTitle(title);
        campaign.setMessage(message);
        campaign.setStatus(status);
        campaign.setTargetAudience(targetAudience);
        campaign.setSentDate(sentDate);
        campaign.setScheduledDate(scheduledDate);
        campaign.setRecipients(recipients);
        campaign.setOpens(opens);
        return campaign;
    }

    private void initWhatsAppContacts() {
        whatsAppContactRepository.save(createWhatsAppContact("Maria Silva", "(11) 99999-1111", WhatsAppContact.ContactStatus.ATIVO, "Agendamento", 2, 2));
        whatsAppContactRepository.save(createWhatsAppContact("Ana Costa", "(11) 99999-2222", WhatsAppContact.ContactStatus.HUMANO, "Atendimento Humano", 5, 1));
        whatsAppContactRepository.save(createWhatsAppContact("Juliana Santos", "(11) 99999-3333", WhatsAppContact.ContactStatus.AGUARDANDO, "Confirmação", 15, 0));
        whatsAppContactRepository.save(createWhatsAppContact("Carla Oliveira", "(11) 99999-4444", WhatsAppContact.ContactStatus.ATIVO, "Boas-vindas", 30, 0));
        whatsAppContactRepository.save(createWhatsAppContact("Patricia Lima", "(11) 99999-5555", WhatsAppContact.ContactStatus.FINALIZADO, "Agendamento Confirmado", 60, 0));
    }

    private WhatsAppContact createWhatsAppContact(String name, String phone, WhatsAppContact.ContactStatus status, String stage, int minutesAgo, int unread) {
        WhatsAppContact contact = new WhatsAppContact();
        contact.setName(name);
        contact.setPhone(phone);
        contact.setStatus(status);
        contact.setStage(stage);
        contact.setLastMessage(LocalDateTime.now().minusMinutes(minutesAgo));
        contact.setUnread(unread);
        return contact;
    }

    private void initConfigurations() {
        configurationRepository.save(createConfig("clinicName", "Musa SPA", "general", "Nome da clínica"));
        configurationRepository.save(createConfig("phone", "(11) 94979-1718", "general", "Telefone principal"));
        configurationRepository.save(createConfig("email", "contato@musasp.com.br", "general", "E-mail de contato"));
        configurationRepository.save(createConfig("address", "Alameda Rio Negro, 1000 - Alphaville", "general", "Endereço"));
        configurationRepository.save(createConfig("newAppointment", "true", "notifications", "Notificar novo agendamento"));
        configurationRepository.save(createConfig("clientConfirmation", "true", "notifications", "Notificar confirmação de cliente"));
        configurationRepository.save(createConfig("humanAttendance", "true", "notifications", "Notificar atendimento humano"));
        configurationRepository.save(createConfig("appointmentReminder", "true", "automation", "Lembrete de agendamento"));
        configurationRepository.save(createConfig("autoConfirmation", "true", "automation", "Confirmação automática"));
        configurationRepository.save(createConfig("postServiceMessage", "false", "automation", "Mensagem pós-atendimento"));
    }

    private spa.musa.send.mensage.entity.Configuration createConfig(String key, String value, String group, String description) {
        spa.musa.send.mensage.entity.Configuration config = new spa.musa.send.mensage.entity.Configuration();
        config.setKey(key);
        config.setValue(value);
        config.setGroup(group);
        config.setDescription(description);
        return config;
    }
}
