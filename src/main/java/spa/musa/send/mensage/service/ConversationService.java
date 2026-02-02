package spa.musa.send.mensage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spa.musa.send.mensage.entity.Contact;
import spa.musa.send.mensage.entity.Conversation;
import spa.musa.send.mensage.entity.Message;
import spa.musa.send.mensage.repository.ContactRepository;
import spa.musa.send.mensage.repository.ConversationRepository;
import spa.musa.send.mensage.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para gerenciar conversas e persistência
 */
@Service
@Transactional
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ContactRepository contactRepository;
    private final MessageRepository messageRepository;

    public ConversationService(ConversationRepository conversationRepository,
                               ContactRepository contactRepository,
                               MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.contactRepository = contactRepository;
        this.messageRepository = messageRepository;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // CONVERSAS
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Obtém ou cria conversa ativa para um número
     */
    public Conversation getOrCreateConversation(String number, String name) {
        // Busca conversa ativa (não fechada)
        Optional<Conversation> existing = conversationRepository
            .findByNumberAndStatusNot(number, Conversation.Status.CLOSED);

        if (existing.isPresent()) {
            Conversation conv = existing.get();
            conv.setLastMessageAt(LocalDateTime.now());
            return conversationRepository.save(conv);
        }

        // Cria nova conversa
        Conversation conv = new Conversation();
        conv.setNumber(number);
        conv.setContactName(name);
        conv.setStatus(Conversation.Status.BOT);
        conv.setCurrentStep(Conversation.Step.INICIO);

        // Atualiza ou cria contato
        getOrCreateContact(number, name);

        return conversationRepository.save(conv);
    }

    /**
     * Atualiza status da conversa
     */
    public Conversation updateStatus(Long conversationId, Conversation.Status status) {
        Conversation conv = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversa não encontrada"));
        
        conv.setStatus(status);
        if (status == Conversation.Status.CLOSED) {
            conv.setClosedAt(LocalDateTime.now());
        }
        
        return conversationRepository.save(conv);
    }

    /**
     * Atribui conversa a um atendente
     */
    public Conversation assignToAgent(Long conversationId, String agentName) {
        Conversation conv = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversa não encontrada"));
        
        conv.setAssignedAgent(agentName);
        conv.setStatus(Conversation.Status.WITH_AGENT);
        
        return conversationRepository.save(conv);
    }

    /**
     * Lista conversas aguardando atendente
     */
    public List<Conversation> getWaitingForAgent() {
        return conversationRepository.findWaitingForAgent();
    }

    /**
     * Lista todas as conversas ativas
     */
    public List<Conversation> getActiveConversations() {
        return conversationRepository.findActiveConversations();
    }

    /**
     * Lista conversas de um atendente
     */
    public List<Conversation> getConversationsByAgent(String agent) {
        return conversationRepository.findByAgent(agent);
    }

    /**
     * Conta conversas aguardando atendente
     */
    public long countWaitingForAgent() {
        return conversationRepository.countWaitingForAgent();
    }

    // ════════════════════════════════════════════════════════════════════════════
    // MENSAGENS
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Salva mensagem recebida
     */
    public Message saveIncomingMessage(Conversation conversation, String content, String whatsappId) {
        Message msg = new Message();
        msg.setConversation(conversation);
        msg.setNumber(conversation.getNumber());
        msg.setDirection(Message.Direction.INCOMING);
        msg.setContent(content);
        msg.setWhatsappId(whatsappId);
        
        return messageRepository.save(msg);
    }

    /**
     * Salva mensagem enviada
     */
    public Message saveOutgoingMessage(Conversation conversation, String content) {
        Message msg = new Message();
        msg.setConversation(conversation);
        msg.setNumber(conversation.getNumber());
        msg.setDirection(Message.Direction.OUTGOING);
        msg.setContent(content);
        
        return messageRepository.save(msg);
    }

    /**
     * Lista mensagens de uma conversa
     */
    public List<Message> getMessages(Long conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // CONTATOS
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Obtém ou cria contato
     */
    public Contact getOrCreateContact(String number, String name) {
        return contactRepository.findByNumber(number)
            .map(contact -> {
                if (name != null && !name.isBlank()) {
                    contact.setName(name);
                }
                contact.setLastInteraction(LocalDateTime.now());
                return contactRepository.save(contact);
            })
            .orElseGet(() -> {
                Contact contact = new Contact();
                contact.setNumber(number);
                contact.setName(name);
                contact.setLastInteraction(LocalDateTime.now());
                return contactRepository.save(contact);
            });
    }

    /**
     * Lista contatos que autorizaram receber mensagens
     */
    public List<Contact> getOptedInContacts() {
        return contactRepository.findByOptedInTrue();
    }

    /**
     * Lista todos os contatos
     */
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    /**
     * Busca contatos por tag
     */
    public List<Contact> getContactsByTag(String tag) {
        return contactRepository.findByTag(tag);
    }
}
