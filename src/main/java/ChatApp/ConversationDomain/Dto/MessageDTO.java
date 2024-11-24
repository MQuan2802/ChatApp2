package ChatApp.ConversationDomain.Dto;

import ChatApp.ConversationDomain.Entity.ChatMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class MessageDTO {

    private String content;

    private ChatMessage.ContentType contentType;

    private Long sender;

//    private String conversationName;

    private Long conversationId;

    public MessageDTO (ChatMessage chatMessage) {
        this.content = String.valueOf(chatMessage.getContent());
        this.contentType = chatMessage.getContentType();
        this.sender = chatMessage.getSender();
        this.conversationId = chatMessage.getConversation().getId();
//        this.conversationName = chatMessage.getConversation().getName();
    }
}
