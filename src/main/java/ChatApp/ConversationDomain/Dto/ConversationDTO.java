package ChatApp.ConversationDomain.Dto;

import ChatApp.ConversationDomain.Entity.Conversation;
import ChatApp.ConversationDomain.Entity.ChatMessage;
import ChatApp.ConversationDomain.Entity.Participant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ConversationDTO {
    List<ChatMessage> chatMessages;

    List<Participant> participants;

    Long creatorId;

    String name;

    Long id;

    public ConversationDTO(Conversation conversation) {
        this.chatMessages = conversation.getChatMessages();
        this.participants = conversation.getParticipants();
        this.creatorId = conversation.getAdmin();
        this.id = conversation.getId();

    }
}
