package spa.musa.send.mensage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spa.musa.send.mensage.entity.*;
import spa.musa.send.mensage.evolution.EvolutionClient;
import spa.musa.send.mensage.repository.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço que gerencia o fluxo completo do chatbot baseado em menus
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatFlowService {

    private final ChatSessionRepository chatSessionRepository;
    private final SpaServiceRepository spaServiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final EvolutionClient evolutionClient;
    private final ConversationService conversationService;

    // Categorias de serviços
    private static final Map<String, String> CATEGORIES = Map.of(
        "1", "Facial",
        "2", "Manicure",
        "3", "Corporal",
        "4", "Cabelo",
        "5", "Pacotes"
    );

    private static final Map<String, String> CATEGORY_EMOJIS = Map.of(
        "Facial", "💆‍♀️",
        "Manicure", "💅",
        "Corporal", "🧖‍♀️",
        "Cabelo", "💇‍♀️",
        "Pacotes", "👰"
    );

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Processa mensagem recebida e retorna resposta apropriada
     */
    public void processMessage(String phoneNumber, String contactName, String message) {
        ChatSession session = getOrCreateSession(phoneNumber);
        String normalizedMessage = message.trim().toLowerCase();

        log.info("📱 Processando: {} | Nível: {} | Mensagem: {}", phoneNumber, session.getMenuLevel(), message);

        // Comando global para voltar ao menu principal
        if (normalizedMessage.equals("menu")) {
            session.resetSession();
            chatSessionRepository.save(session);
            sendMainMenu(session, contactName);
            return;
        }

        // Voltar (0 em qualquer nível exceto menu principal)
        if (normalizedMessage.equals("0")) {
            if (session.getMenuLevel() == ChatSession.MenuLevel.MAIN_MENU) {
                // Já está no menu principal, apenas mostra novamente
                sendMainMenu(session, contactName);
            } else {
                handleBack(session, contactName);
            }
            return;
        }

        // Processar baseado no nível atual
        switch (session.getMenuLevel()) {
            case MAIN_MENU -> handleMainMenu(session, contactName, normalizedMessage);
            
            // Serviços
            case SERVICES_MENU -> handleServicesMenu(session, normalizedMessage);
            case SERVICES_FACIAL, SERVICES_MANICURE, SERVICES_CORPORAL, 
                 SERVICES_CABELO, SERVICES_PACOTES -> handleServiceSelection(session, normalizedMessage);
            case SERVICE_DETAIL -> handleServiceDetailOptions(session, normalizedMessage);
            
            // Preços
            case PRICES_MENU -> handlePricesMenu(session, normalizedMessage);
            case PRICES_FACIAL, PRICES_MANICURE, PRICES_CORPORAL,
                 PRICES_CABELO, PRICES_PACOTES -> handlePriceDetails(session, normalizedMessage);
            
            // Agendamento
            case SCHEDULING_CATEGORY -> handleSchedulingCategory(session, normalizedMessage);
            case SCHEDULING_SERVICE -> handleSchedulingService(session, normalizedMessage);
            case SCHEDULING_DATE -> handleSchedulingDate(session, normalizedMessage);
            case SCHEDULING_TIME -> handleSchedulingTime(session, normalizedMessage);
            case SCHEDULING_NAME -> handleSchedulingName(session, message);
            case SCHEDULING_CPF -> handleSchedulingCpf(session, message);
            case SCHEDULING_PHONE -> handleSchedulingPhone(session, message);
            case SCHEDULING_EMAIL -> handleSchedulingEmail(session, message);
            case SCHEDULING_CONFIRMATION -> handleSchedulingConfirmation(session, normalizedMessage);
            
            // Suporte
            case SUPPORT_MENU -> handleSupportMenu(session, normalizedMessage);
            case SUPPORT_PROBLEM -> handleSupportProblem(session, normalizedMessage);
            case SUPPORT_NAME -> handleSupportName(session, message);
            case SUPPORT_CPF -> handleSupportCpf(session, message);
            case SUPPORT_PHONE -> handleSupportPhone(session, message);
            case SUPPORT_EMAIL -> handleSupportEmail(session, message);
            
            // Atendente
            case ATTENDANT_NAME -> handleAttendantName(session, message);
            case ATTENDANT_CPF -> handleAttendantCpf(session, message);
            case ATTENDANT_PHONE -> handleAttendantPhone(session, message);
            case ATTENDANT_EMAIL -> handleAttendantEmail(session, message);
            case ATTENDANT_MESSAGE -> handleAttendantMessage(session, message);
            case ATTENDANT_QUEUE -> handleAttendantQueue(session, normalizedMessage);
            
            case WAITING_HUMAN -> handleWaitingHuman(session, normalizedMessage);
            
            case FLOW_COMPLETED -> handleFlowCompleted(session, contactName, normalizedMessage);
            
            default -> sendMainMenu(session, contactName);
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // MENU PRINCIPAL
    // ════════════════════════════════════════════════════════════════════════════

    private void sendMainMenu(ChatSession session, String contactName) {
        String name = contactName != null && !contactName.isBlank() ? contactName : "";
        String greeting = name.isEmpty() ? "" : ", " + name;
        
        String message = String.format("""
            Olá%s! ✨

            Seja bem-vinda ao *Musa Spa*.
            Estou aqui para facilitar seu atendimento.

            Como posso ajudá-la hoje?

            *1* - 💅 Ver nossos serviços
            *2* - 💰 Consultar preços e duração
            *3* - 📅 Agendar um horário
            *4* - 🔧 Suporte técnico
            *5* - 👩‍💼 Falar com atendente

            _Digite o número da opção desejada_
            """, greeting);

        sendMessage(session, message);
        session.setMenuLevel(ChatSession.MenuLevel.MAIN_MENU);
        chatSessionRepository.save(session);
    }

    private void handleMainMenu(ChatSession session, String contactName, String message) {
        switch (message) {
            case "1" -> {
                session.setFlowType(ChatSession.FlowType.SERVICES);
                session.setPreviousLevel(ChatSession.MenuLevel.MAIN_MENU);
                sendServicesMenu(session);
            }
            case "2" -> {
                session.setFlowType(ChatSession.FlowType.PRICES);
                session.setPreviousLevel(ChatSession.MenuLevel.MAIN_MENU);
                sendPricesMenu(session);
            }
            case "3" -> {
                session.setFlowType(ChatSession.FlowType.SCHEDULING);
                session.setPreviousLevel(ChatSession.MenuLevel.MAIN_MENU);
                sendSchedulingCategoryMenu(session);
            }
            case "4" -> {
                session.setFlowType(ChatSession.FlowType.SUPPORT);
                session.setPreviousLevel(ChatSession.MenuLevel.MAIN_MENU);
                sendSupportMenu(session);
            }
            case "5" -> {
                session.setFlowType(ChatSession.FlowType.ATTENDANT);
                session.setPreviousLevel(ChatSession.MenuLevel.MAIN_MENU);
                startAttendantFlow(session);
            }
            default -> {
                // Qualquer outra mensagem no menu principal mostra o menu de boas-vindas
                sendMainMenu(session, contactName);
            }
        }
        chatSessionRepository.save(session);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // NÍVEL 1 - SERVIÇOS
    // ════════════════════════════════════════════════════════════════════════════

    private void sendServicesMenu(ChatSession session) {
        String message = """
            ✨ *NOSSOS SERVIÇOS* ✨

            Escolha a categoria para ver os detalhes:

            *1* - 💆‍♀️ Tratamentos Faciais
            *2* - 💅 Manicure e Pedicure
            *3* - 🧖‍♀️ Tratamentos Corporais
            *4* - 💇‍♀️ Cabelo e Estética Capilar
            *5* - 👰 Pacotes Especiais
            *0* - ⬅️ Voltar ao menu principal

            _Digite o número da categoria desejada_
            """;

        sendMessage(session, message);
        session.setMenuLevel(ChatSession.MenuLevel.SERVICES_MENU);
    }

    private void handleServicesMenu(ChatSession session, String message) {
        String category = CATEGORIES.get(message);
        if (category != null) {
            session.setSelectedCategory(category);
            session.setPreviousLevel(ChatSession.MenuLevel.SERVICES_MENU);
            sendServicesByCategory(session, category);
        } else {
            sendInvalidOption(session);
        }
        chatSessionRepository.save(session);
    }

    private void sendServicesByCategory(ChatSession session, String category) {
        List<SpaService> services = spaServiceRepository.findByCategoryAndActiveTrue(category);
        String emoji = CATEGORY_EMOJIS.getOrDefault(category, "✨");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s *%s*\n\n", emoji, category.toUpperCase()));

        int i = 1;
        for (SpaService service : services) {
            sb.append(String.format("*%d* - %s\n", i, service.getName()));
            sb.append("━━━━━━━━━━━━━━━━━━━\n");
            sb.append(service.getDescription() != null ? service.getDescription() : "Serviço especializado");
            sb.append("\n\n");
            i++;
        }

        sb.append("*0* - ⬅️ Voltar\n\n");
        sb.append("_Digite o número para mais informações ou 0 para voltar_");

        sendMessage(session, sb.toString());

        // Define o nível baseado na categoria
        ChatSession.MenuLevel level = switch (category) {
            case "Facial" -> ChatSession.MenuLevel.SERVICES_FACIAL;
            case "Manicure" -> ChatSession.MenuLevel.SERVICES_MANICURE;
            case "Corporal" -> ChatSession.MenuLevel.SERVICES_CORPORAL;
            case "Cabelo" -> ChatSession.MenuLevel.SERVICES_CABELO;
            case "Pacotes" -> ChatSession.MenuLevel.SERVICES_PACOTES;
            default -> ChatSession.MenuLevel.SERVICES_MENU;
        };
        session.setMenuLevel(level);
    }

    private void handleServiceSelection(ChatSession session, String message) {
        List<SpaService> services = spaServiceRepository.findByCategoryAndActiveTrue(session.getSelectedCategory());
        
        try {
            int index = Integer.parseInt(message) - 1;
            if (index >= 0 && index < services.size()) {
                SpaService service = services.get(index);
                session.setPreviousLevel(session.getMenuLevel());
                sendServiceDetail(session, service);
            } else {
                sendInvalidOption(session);
            }
        } catch (NumberFormatException e) {
            sendInvalidOption(session);
        }
        chatSessionRepository.save(session);
    }

    /**
     * Trata as opções quando o usuário está vendo os detalhes de um serviço
     * 1 = Agendar este serviço
     * 0 = Voltar
     */
    private void handleServiceDetailOptions(ChatSession session, String message) {
        switch (message) {
            case "1" -> {
                // Agendar este serviço - já tem o serviço selecionado na sessão
                session.setFlowType(ChatSession.FlowType.SCHEDULING);
                session.setPreviousLevel(ChatSession.MenuLevel.SERVICE_DETAIL);
                sendSchedulingDateMenu(session);
            }
            default -> sendInvalidOption(session);
        }
        chatSessionRepository.save(session);
    }

    private void sendServiceDetail(ChatSession session, SpaService service) {
        String emoji = CATEGORY_EMOJIS.getOrDefault(service.getCategory(), "✨");
        
        String message = String.format("""
            %s *%s*
            ━━━━━━━━━━━━━━━━━━━

            📝 *Descrição:*
            %s

            ⏱️ *Duração:* %d minutos
            💵 *Valor:* R$ %s

            ━━━━━━━━━━━━━━━━━━━

            *1* - 📅 Agendar este serviço
            *0* - ⬅️ Voltar

            _Digite sua opção_
            """, 
            emoji,
            service.getName(),
            service.getDescription() != null ? service.getDescription() : "Serviço especializado do Musa Spa.",
            service.getDuration(),
            formatPrice(service.getPrice())
        );

        sendMessage(session, message);
        session.setSelectedService(service);
        session.setMenuLevel(ChatSession.MenuLevel.SERVICE_DETAIL);
        chatSessionRepository.save(session);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // NÍVEL 2 - PREÇOS
    // ════════════════════════════════════════════════════════════════════════════

    private void sendPricesMenu(ChatSession session) {
        String message = """
            💰 *PREÇOS E DURAÇÃO*

            Escolha a categoria:

            *1* - 💆‍♀️ Tratamentos Faciais
            *2* - 💅 Manicure e Pedicure
            *3* - 🧖‍♀️ Tratamentos Corporais
            *4* - 💇‍♀️ Cabelo e Estética Capilar
            *5* - 👰 Pacotes Especiais
            *0* - ⬅️ Voltar ao menu principal

            _Digite o número da categoria desejada_
            """;

        sendMessage(session, message);
        session.setMenuLevel(ChatSession.MenuLevel.PRICES_MENU);
    }

    private void handlePricesMenu(ChatSession session, String message) {
        String category = CATEGORIES.get(message);
        if (category != null) {
            session.setSelectedCategory(category);
            session.setPreviousLevel(ChatSession.MenuLevel.PRICES_MENU);
            sendPricesByCategory(session, category);
        } else {
            sendInvalidOption(session);
        }
        chatSessionRepository.save(session);
    }

    private void sendPricesByCategory(ChatSession session, String category) {
        List<SpaService> services = spaServiceRepository.findByCategoryAndActiveTrue(category);
        String emoji = CATEGORY_EMOJIS.getOrDefault(category, "✨");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s *%s*\n", emoji, category.toUpperCase()));
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        for (SpaService service : services) {
            sb.append(String.format("*%s*\n", service.getName()));
            sb.append(String.format("⏱️ Duração: %d min\n", service.getDuration()));
            sb.append(String.format("💵 Valor: R$ %s\n\n", formatPrice(service.getPrice())));
        }

        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        sb.append("*1* - 📅 Agendar um desses serviços\n");
        sb.append("*0* - ⬅️ Voltar\n\n");
        sb.append("_Digite sua opção_");

        sendMessage(session, sb.toString());

        ChatSession.MenuLevel level = switch (category) {
            case "Facial" -> ChatSession.MenuLevel.PRICES_FACIAL;
            case "Manicure" -> ChatSession.MenuLevel.PRICES_MANICURE;
            case "Corporal" -> ChatSession.MenuLevel.PRICES_CORPORAL;
            case "Cabelo" -> ChatSession.MenuLevel.PRICES_CABELO;
            case "Pacotes" -> ChatSession.MenuLevel.PRICES_PACOTES;
            default -> ChatSession.MenuLevel.PRICES_MENU;
        };
        session.setMenuLevel(level);
    }

    private void handlePriceDetails(ChatSession session, String message) {
        if (message.equals("1")) {
            // Ir para agendamento mantendo a categoria selecionada
            session.setFlowType(ChatSession.FlowType.SCHEDULING);
            session.setPreviousLevel(session.getMenuLevel());
            sendSchedulingServiceMenu(session, session.getSelectedCategory());
        } else {
            sendInvalidOption(session);
        }
        chatSessionRepository.save(session);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // NÍVEL 3 - AGENDAMENTO
    // ════════════════════════════════════════════════════════════════════════════

    private void sendSchedulingCategoryMenu(ChatSession session) {
        String message = """
            📅 *AGENDAMENTO*

            Vou te ajudar a agendar seu horário!

            Primeiro, escolha o serviço desejado:

            *1* - 💆‍♀️ Tratamentos Faciais
            *2* - 💅 Manicure e Pedicure
            *3* - 🧖‍♀️ Tratamentos Corporais
            *4* - 💇‍♀️ Cabelo e Estética Capilar
            *5* - 👰 Pacotes Especiais
            *0* - ⬅️ Voltar ao menu principal

            _Digite o número da categoria_
            """;

        sendMessage(session, message);
        session.setMenuLevel(ChatSession.MenuLevel.SCHEDULING_CATEGORY);
    }

    private void handleSchedulingCategory(ChatSession session, String message) {
        String category = CATEGORIES.get(message);
        if (category != null) {
            session.setSelectedCategory(category);
            session.setPreviousLevel(ChatSession.MenuLevel.SCHEDULING_CATEGORY);
            sendSchedulingServiceMenu(session, category);
        } else {
            sendInvalidOption(session);
        }
        chatSessionRepository.save(session);
    }

    private void sendSchedulingServiceMenu(ChatSession session, String category) {
        List<SpaService> services = spaServiceRepository.findByCategoryAndActiveTrue(category);
        String emoji = CATEGORY_EMOJIS.getOrDefault(category, "✨");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s *QUAL %s?*\n\n", emoji, category.toUpperCase()));

        int i = 1;
        for (SpaService service : services) {
            sb.append(String.format("*%d* - %s (%dmin - R$%s)\n",
                    i, service.getName(), service.getDuration(), formatPrice(service.getPrice())));
            i++;
        }

        sb.append("*0* - ⬅️ Voltar\n\n");
        sb.append("_Digite o número do serviço_");

        sendMessage(session, sb.toString());
        session.setMenuLevel(ChatSession.MenuLevel.SCHEDULING_SERVICE);
    }

    private void handleSchedulingService(ChatSession session, String message) {
        List<SpaService> services = spaServiceRepository.findByCategoryAndActiveTrue(session.getSelectedCategory());
        
        try {
            int index = Integer.parseInt(message) - 1;
            if (index >= 0 && index < services.size()) {
                session.setSelectedService(services.get(index));
                session.setPreviousLevel(ChatSession.MenuLevel.SCHEDULING_SERVICE);
                sendSchedulingDateMenu(session);
            } else {
                sendInvalidOption(session);
            }
        } catch (NumberFormatException e) {
            sendInvalidOption(session);
        }
        chatSessionRepository.save(session);
    }

    private void sendSchedulingDateMenu(ChatSession session) {
        SpaService service = session.getSelectedService();
        List<LocalDate> availableDates = getAvailableDates();

        StringBuilder sb = new StringBuilder();
        sb.append("📅 *ESCOLHA A DATA*\n\n");
        sb.append(String.format("Você selecionou:\n✨ %s (%dmin - R$%s)\n\n",
                service.getName(), service.getDuration(), formatPrice(service.getPrice())));
        sb.append("Escolha uma data disponível:\n\n");

        int i = 1;
        for (LocalDate date : availableDates) {
            String dayName = getDayName(date.getDayOfWeek());
            sb.append(String.format("*%d* - %s, %s\n", i, dayName, date.format(DATE_FORMATTER)));
            i++;
        }

        sb.append("*0* - ⬅️ Voltar\n\n");
        sb.append("_Digite o número da data desejada_");

        sendMessage(session, sb.toString());
        session.setMenuLevel(ChatSession.MenuLevel.SCHEDULING_DATE);
    }

    private void handleSchedulingDate(ChatSession session, String message) {
        List<LocalDate> availableDates = getAvailableDates();
        
        try {
            int index = Integer.parseInt(message) - 1;
            if (index >= 0 && index < availableDates.size()) {
                session.setSelectedDate(availableDates.get(index));
                session.setPreviousLevel(ChatSession.MenuLevel.SCHEDULING_DATE);
                sendSchedulingTimeMenu(session);
            } else {
                sendInvalidOption(session);
            }
        } catch (NumberFormatException e) {
            sendInvalidOption(session);
        }
        chatSessionRepository.save(session);
    }

    private void sendSchedulingTimeMenu(ChatSession session) {
        SpaService service = session.getSelectedService();
        LocalDate date = session.getSelectedDate();
        List<LocalTime> availableTimes = getAvailableTimes(date, service.getDuration());

        StringBuilder sb = new StringBuilder();
        sb.append("⏰ *ESCOLHA O HORÁRIO*\n\n");
        sb.append(String.format("Data selecionada: %s, %s\n", getDayName(date.getDayOfWeek()), date.format(DATE_FORMATTER)));
        sb.append(String.format("Serviço: %s (%dmin)\n\n", service.getName(), service.getDuration()));
        sb.append("Horários disponíveis:\n\n");

        if (availableTimes.isEmpty()) {
            sb.append("❌ Não há horários disponíveis nesta data.\n\n");
            sb.append("*0* - ⬅️ Voltar e escolher outra data\n");
        } else {
            int i = 1;
            for (LocalTime time : availableTimes) {
                sb.append(String.format("*%d* - %s\n", i, time.format(TIME_FORMATTER)));
                i++;
            }
            sb.append("*0* - ⬅️ Voltar\n\n");
            sb.append("_Digite o número do horário desejado_");
        }

        sendMessage(session, sb.toString());
        session.setMenuLevel(ChatSession.MenuLevel.SCHEDULING_TIME);
    }

    private void handleSchedulingTime(ChatSession session, String message) {
        List<LocalTime> availableTimes = getAvailableTimes(session.getSelectedDate(), session.getSelectedService().getDuration());
        
        try {
            int index = Integer.parseInt(message) - 1;
            if (index >= 0 && index < availableTimes.size()) {
                session.setSelectedTime(availableTimes.get(index));
                session.setPreviousLevel(ChatSession.MenuLevel.SCHEDULING_TIME);
                startDataCollection(session);
            } else {
                sendInvalidOption(session);
            }
        } catch (NumberFormatException e) {
            sendInvalidOption(session);
        }
        chatSessionRepository.save(session);
    }

    private void startDataCollection(ChatSession session) {
        String message = """
            📝 *SEUS DADOS*

            Para confirmar seu agendamento,
            preciso de algumas informações:

            Por favor, envie seu *nome completo*

            _Aguardo sua resposta..._
            """;

        sendMessage(session, message);
        session.setMenuLevel(ChatSession.MenuLevel.SCHEDULING_NAME);
    }

    private void handleSchedulingName(ChatSession session, String name) {
        if (name.length() < 3) {
            sendMessage(session, "❌ Por favor, informe seu nome completo.");
            return;
        }
        
        session.setTempName(name);
        chatSessionRepository.save(session);

        String message = String.format("""
            Obrigada, *%s*!

            Agora preciso do seu *CPF*
            (apenas números)

            _Aguardo..._
            """, name);

        sendMessage(session, message);
        session.setMenuLevel(ChatSession.MenuLevel.SCHEDULING_CPF);
        chatSessionRepository.save(session);
    }

    private void handleSchedulingCpf(ChatSession session, String cpf) {
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        
        if (cleanCpf.length() != 11) {
            sendMessage(session, "❌ CPF inválido. Por favor, digite apenas os 11 números do CPF.");
            return;
        }

        session.setTempCpf(cleanCpf);
        chatSessionRepository.save(session);

        String message = """
            Perfeito!

            Qual seu *telefone com DDD*?
            (Ex: 11987654321)

            _Aguardo..._
            """;

        sendMessage(session, message);
        session.setMenuLevel(ChatSession.MenuLevel.SCHEDULING_PHONE);
        chatSessionRepository.save(session);
    }

    private void handleSchedulingPhone(ChatSession session, String phone) {
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        
        if (cleanPhone.length() < 10 || cleanPhone.length() > 11) {
            sendMessage(session, "❌ Telefone inválido. Por favor, digite o DDD + número (Ex: 11987654321).");
            return;
        }

        session.setTempPhone(cleanPhone);
        chatSessionRepository.save(session);

        String message = """
            Ótimo!

            Por último, qual seu *e-mail*?

            _Aguardo..._
            """;

        sendMessage(session, message);
        session.setMenuLevel(ChatSession.MenuLevel.SCHEDULING_EMAIL);
        chatSessionRepository.save(session);
    }

    private void handleSchedulingEmail(ChatSession session, String email) {
        if (!email.contains("@") || !email.contains(".")) {
            sendMessage(session, "❌ E-mail inválido. Por favor, digite um e-mail válido.");
            return;
        }

        session.setTempEmail(email);
        chatSessionRepository.save(session);

        sendConfirmationSummary(session);
    }

    private void sendConfirmationSummary(ChatSession session) {
        SpaService service = session.getSelectedService();
        LocalDate date = session.getSelectedDate();
        LocalTime time = session.getSelectedTime();

        String cpfMasked = maskCpf(session.getTempCpf());
        String phoneMasked = maskPhone(session.getTempPhone());

        String message = String.format("""
            ✅ *CONFIRMAÇÃO DE AGENDAMENTO*

            ━━━━━━━━━━━━━━━━━━━━━━━━
            👤 Nome: %s
            📄 CPF: %s
            📱 Tel: %s
            📧 Email: %s

            🗓️ DATA: %s, %s
            🕐 HORÁRIO: %s
            ⏱️ DURAÇÃO: %d minutos
            💅 SERVIÇO: %s
            💰 VALOR: R$ %s
            ━━━━━━━━━━━━━━━━━━━━━━━━

            Você receberá:
            ✓ Confirmação por e-mail
            ✓ Lembrete por WhatsApp 24h antes

            *1* - ✅ Confirmar agendamento
            *2* - ✏️ Corrigir alguma informação
            *0* - ❌ Cancelar

            _Digite sua opção_
            """,
            session.getTempName(),
            cpfMasked,
            phoneMasked,
            session.getTempEmail(),
            getDayName(date.getDayOfWeek()),
            date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            time.format(TIME_FORMATTER),
            service.getDuration(),
            service.getName(),
            formatPrice(service.getPrice())
        );

        sendMessage(session, message);
        session.setMenuLevel(ChatSession.MenuLevel.SCHEDULING_CONFIRMATION);
        chatSessionRepository.save(session);
    }

    private void handleSchedulingConfirmation(ChatSession session, String message) {
        switch (message) {
            case "1" -> confirmAppointment(session);
            case "2" -> {
                // Reinicia coleta de dados
                session.setTempName(null);
                session.setTempCpf(null);
                session.setTempPhone(null);
                session.setTempEmail(null);
                startDataCollection(session);
            }
            case "0" -> {
                sendMessage(session, "❌ Agendamento cancelado.\n\nDigite *menu* para voltar ao início.");
                session.resetSession();
            }
            default -> sendInvalidOption(session);
        }
        chatSessionRepository.save(session);
    }

    private void confirmAppointment(ChatSession session) {
        // Busca ou cria cliente pelo CPF
        Client client = clientRepository.findByCpf(session.getTempCpf())
            .orElseGet(() -> {
                Client newClient = Client.builder()
                    .name(session.getTempName())
                    .cpf(session.getTempCpf())
                    .phone(session.getTempPhone())
                    .email(session.getTempEmail())
                    .build();
                return clientRepository.save(newClient);
            });

        // Atualiza dados do cliente se necessário
        client.setName(session.getTempName());
        client.setPhone(session.getTempPhone());
        client.setEmail(session.getTempEmail());
        clientRepository.save(client);

        // Cria agendamento
        Appointment appointment = Appointment.builder()
            .client(client)
            .service(session.getSelectedService())
            .date(session.getSelectedDate())
            .time(session.getSelectedTime())
            .room("Sala 1") // Pode ser implementada lógica de salas
            .status(Appointment.AppointmentStatus.PENDENTE)
            .build();
        
        appointmentRepository.save(appointment);

        String confirmationMessage = String.format("""
            🎉 *AGENDAMENTO CONFIRMADO!*

            ━━━━━━━━━━━━━━━━━━━━━━━━
            📋 Código: #%d
            
            🗓️ %s, %s às %s
            💅 %s
            💰 R$ %s
            ━━━━━━━━━━━━━━━━━━━━━━━━

            ✅ Confirmação enviada para:
            📧 %s
            📱 %s

            Aguardamos você! 💕

            *1* - 🏠 Voltar ao menu principal
            *2* - ❌ Encerrar atendimento

            _Digite sua opção_
            """,
            appointment.getId(),
            getDayName(session.getSelectedDate().getDayOfWeek()),
            session.getSelectedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            session.getSelectedTime().format(TIME_FORMATTER),
            session.getSelectedService().getName(),
            formatPrice(session.getSelectedService().getPrice()),
            session.getTempEmail(),
            formatPhoneDisplay(session.getTempPhone())
        );

        sendMessage(session, confirmationMessage);
        // Define estado para aguardar escolha 1 (menu) ou 2 (encerrar)
        session.setMenuLevel(ChatSession.MenuLevel.FLOW_COMPLETED);
        chatSessionRepository.save(session);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // NÍVEL 4 - SUPORTE
    // ════════════════════════════════════════════════════════════════════════════

    private void sendSupportMenu(ChatSession session) {
        String message = """
            🔧 *SUPORTE TÉCNICO*

            Qual problema você está enfrentando?

            *1* - ❌ Não consigo agendar
            *2* - 🔄 Quero remarcar um horário
            *3* - 📅 Preciso cancelar um agendamento
            *4* - 💳 Dúvidas sobre pagamento
            *5* - 🎁 Não recebi meu cupom/voucher
            *6* - 📱 Problemas com confirmação
            *7* - 🆘 Outro problema
            *0* - ⬅️ Voltar ao menu principal

            _Digite o número da sua situação_
            """;

        sendMessage(session, message);
        session.setMenuLevel(ChatSession.MenuLevel.SUPPORT_MENU);
    }

    private void handleSupportMenu(ChatSession session, String message) {
        if (message.matches("[1-7]")) {
            session.setTempMessage(getSupportProblemDescription(message));
            session.setPreviousLevel(ChatSession.MenuLevel.SUPPORT_MENU);
            sendSupportProblemDetails(session, message);
        } else {
            sendInvalidOption(session);
        }
        chatSessionRepository.save(session);
    }

    private String getSupportProblemDescription(String option) {
        return switch (option) {
            case "1" -> "Não consigo agendar";
            case "2" -> "Remarcar horário";
            case "3" -> "Cancelar agendamento";
            case "4" -> "Dúvidas sobre pagamento";
            case "5" -> "Não recebi cupom/voucher";
            case "6" -> "Problemas com confirmação";
            case "7" -> "Outro problema";
            default -> "Suporte";
        };
    }

    private void sendSupportProblemDetails(ChatSession session, String option) {
        String message;
        
        if (option.equals("1")) {
            message = """
                ❌ *PROBLEMA COM AGENDAMENTO*

                Me ajude a entender melhor:

                *1* - Não aparecem horários disponíveis
                *2* - O sistema não aceita meus dados
                *3* - A data que quero não está disponível
                *4* - Outro motivo
                *0* - ⬅️ Voltar

                _Digite o número da situação_
                """;
            session.setMenuLevel(ChatSession.MenuLevel.SUPPORT_PROBLEM);
        } else {
            message = """
                Entendi! Vou transferir você para
                um atendente que vai verificar.

                Por favor, me informe:

                *Nome completo:*
                _Aguardo..._
                """;
            session.setMenuLevel(ChatSession.MenuLevel.SUPPORT_NAME);
        }

        sendMessage(session, message);
        chatSessionRepository.save(session);
    }

    private void handleSupportProblem(ChatSession session, String message) {
        if (message.matches("[1-4]")) {
            String detailMessage = """
                Entendi! Vou transferir você para
                um atendente que vai verificar a
                disponibilidade em nosso sistema.

                Por favor, me informe:

                *Nome completo:*
                _Aguardo..._
                """;
            sendMessage(session, detailMessage);
            session.setMenuLevel(ChatSession.MenuLevel.SUPPORT_NAME);
        } else {
            sendInvalidOption(session);
        }
        chatSessionRepository.save(session);
    }

    private void handleSupportName(ChatSession session, String name) {
        if (name.length() < 3) {
            sendMessage(session, "❌ Por favor, informe seu nome completo.");
            return;
        }
        
        session.setTempName(name);
        sendMessage(session, String.format("Obrigada, *%s*!\n\nAgora preciso do seu *CPF*\n(apenas números)\n\n_Aguardo..._", name));
        session.setMenuLevel(ChatSession.MenuLevel.SUPPORT_CPF);
        chatSessionRepository.save(session);
    }

    private void handleSupportCpf(ChatSession session, String cpf) {
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        
        if (cleanCpf.length() != 11) {
            sendMessage(session, "❌ CPF inválido. Por favor, digite apenas os 11 números.");
            return;
        }

        session.setTempCpf(cleanCpf);
        sendMessage(session, "Perfeito!\n\nQual seu *telefone com DDD*?\n(Ex: 11987654321)\n\n_Aguardo..._");
        session.setMenuLevel(ChatSession.MenuLevel.SUPPORT_PHONE);
        chatSessionRepository.save(session);
    }

    private void handleSupportPhone(ChatSession session, String phone) {
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        
        if (cleanPhone.length() < 10) {
            sendMessage(session, "❌ Telefone inválido. Por favor, digite o DDD + número.");
            return;
        }

        session.setTempPhone(cleanPhone);
        sendMessage(session, "Ótimo!\n\nPor último, qual seu *e-mail*?\n\n_Aguardo..._");
        session.setMenuLevel(ChatSession.MenuLevel.SUPPORT_EMAIL);
        chatSessionRepository.save(session);
    }

    private void handleSupportEmail(ChatSession session, String email) {
        if (!email.contains("@")) {
            sendMessage(session, "❌ E-mail inválido. Por favor, digite um e-mail válido.");
            return;
        }

        session.setTempEmail(email);
        
        // Gera ticket
        String ticketNumber = generateTicketNumber();
        session.setTicketNumber(ticketNumber);

        // Salva ou atualiza cliente
        saveOrUpdateClient(session);

        String message = String.format("""
            ✅ *Registrado!*

            Um atendente entrará em contato
            em até 30 minutos.

            Ticket: *#%s*

            Algo mais que posso ajudar?

            *1* - 🏠 Voltar ao menu principal
            *2* - ❌ Encerrar atendimento

            _Digite sua opção_
            """, ticketNumber);

        sendMessage(session, message);
        session.setMenuLevel(ChatSession.MenuLevel.FLOW_COMPLETED);
        chatSessionRepository.save(session);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // NÍVEL 5 - ATENDENTE
    // ════════════════════════════════════════════════════════════════════════════

    private void startAttendantFlow(ChatSession session) {
        String message = """
            👩‍💼 *FALAR COM ATENDENTE*

            Vou conectar você com nossa equipe!

            Para agilizar, preciso de alguns dados:

            Por favor, envie seu *nome completo*

            _Aguardo sua resposta..._
            """;

        sendMessage(session, message);
        session.setMenuLevel(ChatSession.MenuLevel.ATTENDANT_NAME);
        chatSessionRepository.save(session);
    }

    private void handleAttendantName(ChatSession session, String name) {
        if (name.length() < 3) {
            sendMessage(session, "❌ Por favor, informe seu nome completo.");
            return;
        }
        
        session.setTempName(name);
        sendMessage(session, String.format("Obrigada, *%s*!\n\nAgora preciso do seu *CPF*\n(apenas números)\n\n_Aguardo..._", name));
        session.setMenuLevel(ChatSession.MenuLevel.ATTENDANT_CPF);
        chatSessionRepository.save(session);
    }

    private void handleAttendantCpf(ChatSession session, String cpf) {
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        
        if (cleanCpf.length() != 11) {
            sendMessage(session, "❌ CPF inválido. Por favor, digite apenas os 11 números.");
            return;
        }

        session.setTempCpf(cleanCpf);
        sendMessage(session, "Perfeito!\n\nQual seu *telefone com DDD*?\n(Ex: 11987654321)\n\n_Aguardo..._");
        session.setMenuLevel(ChatSession.MenuLevel.ATTENDANT_PHONE);
        chatSessionRepository.save(session);
    }

    private void handleAttendantPhone(ChatSession session, String phone) {
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        
        if (cleanPhone.length() < 10) {
            sendMessage(session, "❌ Telefone inválido. Por favor, digite o DDD + número.");
            return;
        }

        session.setTempPhone(cleanPhone);
        sendMessage(session, "Ótimo!\n\nPor último, qual seu *e-mail*?\n\n_Aguardo..._");
        session.setMenuLevel(ChatSession.MenuLevel.ATTENDANT_EMAIL);
        chatSessionRepository.save(session);
    }

    private void handleAttendantEmail(ChatSession session, String email) {
        if (!email.contains("@")) {
            sendMessage(session, "❌ E-mail inválido. Por favor, digite um e-mail válido.");
            return;
        }

        session.setTempEmail(email);

        String message = String.format("""
            📝 *DADOS REGISTRADOS*

            Nome: %s
            CPF: %s
            Telefone: %s
            E-mail: %s

            Antes de transferir, me conte
            brevemente sobre o que precisa:

            _Digite sua mensagem..._
            """,
            session.getTempName(),
            maskCpf(session.getTempCpf()),
            maskPhone(session.getTempPhone()),
            session.getTempEmail()
        );

        sendMessage(session, message);
        session.setMenuLevel(ChatSession.MenuLevel.ATTENDANT_MESSAGE);
        chatSessionRepository.save(session);
    }

    private void handleAttendantMessage(ChatSession session, String message) {
        session.setTempMessage(message);
        
        // Gera ticket
        String ticketNumber = generateTicketNumber();
        session.setTicketNumber(ticketNumber);

        // Salva ou atualiza cliente
        saveOrUpdateClient(session);

        // Simula posição na fila
        int queuePosition = (int) (Math.random() * 3) + 1;
        int estimatedMinutes = queuePosition * 2;

        String queueMessage = String.format("""
            ✅ *Perfeito!*

            Você está na fila de atendimento.
            Posição: *%dº*

            Tempo estimado: *%d minutos*

            Aguarde que logo uma atendente
            irá conversar com você! 💕

            ━━━━━━━━━━━━━━━━━━━━━━━━
            Ticket: *#%s*
            Protocolo enviado para seu e-mail
            ━━━━━━━━━━━━━━━━━━━━━━━━
            """, queuePosition, estimatedMinutes, ticketNumber);

        sendMessage(session, queueMessage);
        session.setMenuLevel(ChatSession.MenuLevel.WAITING_HUMAN);
        chatSessionRepository.save(session);
    }

    private void handleAttendantQueue(ChatSession session, String message) {
        handleWaitingHuman(session, message);
    }

    private void handleWaitingHuman(ChatSession session, String message) {
        if (message.equals("1") || message.equals("menu")) {
            session.resetSession();
            sendMainMenu(session, null);
        } else if (message.equals("2")) {
            sendGoodbye(session);
            session.resetSession();
        } else {
            sendMessage(session, "⏳ Você está na fila de atendimento.\n\nDigite *1* para menu principal ou *2* para encerrar.");
        }
        chatSessionRepository.save(session);
    }

    /**
     * Trata o estado após um fluxo ser completado (agendamento confirmado, suporte registrado, etc)
     * Opção 1 = Voltar ao menu principal
     * Opção 2 = Encerrar atendimento
     */
    private void handleFlowCompleted(ChatSession session, String contactName, String message) {
        switch (message) {
            case "1", "menu" -> {
                session.resetSession();
                sendMainMenu(session, contactName);
            }
            case "2" -> {
                sendGoodbye(session);
                session.resetSession();
            }
            default -> {
                // Qualquer outra mensagem mostra as opções novamente
                sendMessage(session, """
                    Digite uma opção válida:
                    
                    *1* - 🏠 Voltar ao menu principal
                    *2* - ❌ Encerrar atendimento
                    """);
            }
        }
        chatSessionRepository.save(session);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // MÉTODOS AUXILIARES
    // ════════════════════════════════════════════════════════════════════════════

    private ChatSession getOrCreateSession(String phoneNumber) {
        return chatSessionRepository.findByPhoneNumber(phoneNumber)
            .orElseGet(() -> {
                ChatSession session = ChatSession.builder()
                    .phoneNumber(phoneNumber)
                    .menuLevel(ChatSession.MenuLevel.MAIN_MENU)
                    .flowType(ChatSession.FlowType.NONE)
                    .build();
                return chatSessionRepository.save(session);
            });
    }

    private void handleBack(ChatSession session, String contactName) {
        ChatSession.MenuLevel currentLevel = session.getMenuLevel();
        ChatSession.MenuLevel previousLevel = session.getPreviousLevel();
        
        // Lógica de navegação baseada no nível atual
        switch (currentLevel) {
            // Se está no menu de serviços, volta pro menu principal
            case SERVICES_MENU, PRICES_MENU, SCHEDULING_CATEGORY, SUPPORT_MENU -> {
                session.resetSession();
                sendMainMenu(session, contactName);
            }
            
            // Se está em uma categoria de serviços, volta pro menu de serviços
            case SERVICES_FACIAL, SERVICES_MANICURE, SERVICES_CORPORAL, 
                 SERVICES_CABELO, SERVICES_PACOTES -> {
                session.setMenuLevel(ChatSession.MenuLevel.SERVICES_MENU);
                sendServicesMenu(session);
            }
            
            // Se está vendo detalhe de um serviço, volta pra lista da categoria
            case SERVICE_DETAIL -> {
                if (previousLevel != null) {
                    session.setMenuLevel(previousLevel);
                    sendServicesByCategory(session, session.getSelectedCategory());
                } else {
                    session.setMenuLevel(ChatSession.MenuLevel.SERVICES_MENU);
                    sendServicesMenu(session);
                }
            }
            
            // Se está em uma categoria de preços, volta pro menu de preços
            case PRICES_FACIAL, PRICES_MANICURE, PRICES_CORPORAL,
                 PRICES_CABELO, PRICES_PACOTES -> {
                session.setMenuLevel(ChatSession.MenuLevel.PRICES_MENU);
                sendPricesMenu(session);
            }
            
            // Agendamento - navegação hierárquica
            case SCHEDULING_SERVICE -> {
                session.setMenuLevel(ChatSession.MenuLevel.SCHEDULING_CATEGORY);
                sendSchedulingCategoryMenu(session);
            }
            case SCHEDULING_DATE -> {
                session.setMenuLevel(ChatSession.MenuLevel.SCHEDULING_SERVICE);
                sendSchedulingServiceMenu(session, session.getSelectedCategory());
            }
            case SCHEDULING_TIME -> {
                session.setMenuLevel(ChatSession.MenuLevel.SCHEDULING_DATE);
                sendSchedulingDateMenu(session);
            }
            
            // Suporte - volta pro menu de suporte
            case SUPPORT_PROBLEM, SUPPORT_NAME, SUPPORT_CPF, SUPPORT_PHONE, SUPPORT_EMAIL -> {
                session.setMenuLevel(ChatSession.MenuLevel.SUPPORT_MENU);
                sendSupportMenu(session);
            }
            
            // Atendente - volta pro menu principal
            case ATTENDANT_NAME, ATTENDANT_CPF, ATTENDANT_PHONE, 
                 ATTENDANT_EMAIL, ATTENDANT_MESSAGE -> {
                session.resetSession();
                sendMainMenu(session, contactName);
            }
            
            // Padrão: volta ao menu principal
            default -> {
                session.resetSession();
                sendMainMenu(session, contactName);
            }
        }
        chatSessionRepository.save(session);
    }

    private void sendMessage(ChatSession session, String message) {
        evolutionClient.sendText(session.getPhoneNumber(), message);
    }

    private void sendInvalidOption(ChatSession session) {
        String message = """
            ❌ *Opção inválida*

            Por favor, digite apenas o número
            correspondente à opção desejada.

            _Tente novamente..._
            """;
        sendMessage(session, message);
    }

    private void sendGoodbye(ChatSession session) {
        String message = """
            💕 *Obrigada pelo contato!*

            Foi um prazer atendê-la.

            *Musa Spa* - Onde sua beleza floresce ✨

            _Digite qualquer mensagem para iniciar um novo atendimento_
            """;
        sendMessage(session, message);
    }

    private List<LocalDate> getAvailableDates() {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // Próximos 7 dias úteis (exceto domingo)
        int count = 0;
        LocalDate date = today.plusDays(1);
        
        while (count < 6) {
            if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dates.add(date);
                count++;
            }
            date = date.plusDays(1);
        }
        
        return dates;
    }

    private List<LocalTime> getAvailableTimes(LocalDate date, int serviceDuration) {
        // Busca agendamentos existentes para a data
        List<Appointment> existingAppointments = appointmentRepository.findByDateOrderByTime(date);
        
        // Horários base (9h às 18h)
        List<LocalTime> allTimes = Arrays.asList(
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            LocalTime.of(13, 0),
            LocalTime.of(14, 0),
            LocalTime.of(15, 0),
            LocalTime.of(16, 0),
            LocalTime.of(17, 0)
        );

        // Filtra horários ocupados
        Set<LocalTime> occupiedTimes = existingAppointments.stream()
            .filter(a -> a.getStatus() != Appointment.AppointmentStatus.CANCELADO)
            .map(Appointment::getTime)
            .collect(Collectors.toSet());

        return allTimes.stream()
            .filter(time -> !occupiedTimes.contains(time))
            .collect(Collectors.toList());
    }

    private String getDayName(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "Segunda";
            case TUESDAY -> "Terça";
            case WEDNESDAY -> "Quarta";
            case THURSDAY -> "Quinta";
            case FRIDAY -> "Sexta";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    private String formatPrice(BigDecimal price) {
        return String.format("%.2f", price).replace(".", ",");
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return "***." + cpf.substring(3, 6) + ".***-" + cpf.substring(9, 11);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 10) return phone;
        String ddd = phone.substring(0, 2);
        String lastDigits = phone.substring(phone.length() - 4);
        return "(" + ddd + ") *****-" + lastDigits;
    }

    private String formatPhoneDisplay(String phone) {
        if (phone == null || phone.length() < 10) return phone;
        String ddd = phone.substring(0, 2);
        String number = phone.substring(2);
        if (number.length() == 9) {
            return "(" + ddd + ") " + number.substring(0, 5) + "-" + number.substring(5);
        }
        return "(" + ddd + ") " + number.substring(0, 4) + "-" + number.substring(4);
    }

    private String generateTicketNumber() {
        return String.valueOf(10000 + new Random().nextInt(90000));
    }

    private void saveOrUpdateClient(ChatSession session) {
        Client client = clientRepository.findByCpf(session.getTempCpf())
            .orElseGet(() -> Client.builder()
                .cpf(session.getTempCpf())
                .build());

        client.setName(session.getTempName());
        client.setPhone(session.getTempPhone());
        client.setEmail(session.getTempEmail());
        
        clientRepository.save(client);
    }
}
