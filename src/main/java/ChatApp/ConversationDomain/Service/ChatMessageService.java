package ChatApp.ConversationDomain.Service;

import ChatApp.ConversationDomain.ChatMessageSpecs;
import ChatApp.ConversationDomain.Entity.ChatMessage;
import ChatApp.ConversationDomain.Entity.Conversation;
import ChatApp.ConversationDomain.Repository.ChatMessageRepository;
import ChatApp.ConversationDomain.Repository.ConversationRepository;
import ChatApp.ConversationDomain.Request.FetchMessageRequest;
import ChatApp.UserDomain.Entity.SpecificationsBuilder;
import ChatApp.MessageExchangeDomain.Dto.SocketMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository repository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Transactional
    public ChatMessage createMessage(SocketMessageDto messageDto){
        Assert.isTrue(Objects.isNull(messageDto.getConversationId()), "Failed to add new message (Reason: invalid conversationId).");
        Conversation conversation = this.conversationRepository.findById(messageDto.getConversationId()).orElse(null);

        Assert.isTrue(Objects.isNull(conversation), "Failed to add new message (Reason: can not find conversation).");

        conversation.setLastMessageTime(Calendar.getInstance());
        ChatMessage newMessage = ChatMessage.builder()
                .sender(messageDto.getSenderId())
                .contentType(messageDto.getContentType())
                .content(messageDto.getContent())
                .conversation(conversation)
                .build();
        return this.repository.save(newMessage);
    }

    public Iterable<ChatMessage> queryWithSpecification(FetchMessageRequest request) {
        SpecificationsBuilder<ChatMessage> specificationsBuilder = new SpecificationsBuilder();
        specificationsBuilder.addSpecification(ChatMessageSpecs.filterByIds(request.getMessageIds()));
        specificationsBuilder.addSpecification(ChatMessageSpecs.filterBySenders(request.getSenderIds()));
        specificationsBuilder.addSpecification(ChatMessageSpecs.filterByConversations(request.getConversationId()));
        specificationsBuilder.addSpecification(ChatMessageSpecs.filterByCreationTime(request.getStartTs(), request.getEndTs()));
        return this.repository.findAll(specificationsBuilder.build(), new PageRequest(request.getPage(), request.getPageSize(), request.getSortDir(), request.getSortProperty()));
    }
}
