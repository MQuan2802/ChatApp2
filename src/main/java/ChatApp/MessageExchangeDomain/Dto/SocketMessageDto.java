package ChatApp.MessageExchangeDomain.Dto;

import ChatApp.ConversationDomain.Entity.ChatMessage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Calendar;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SocketMessageDto {
    @JsonProperty("senderId")
    private Long senderId;

    @JsonProperty("senderParticipantId")
    private Long senderParticipantId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("conversationId")
    private Long conversationId;

    @JsonProperty("contentType")
    private ChatMessage.ContentType contentType;

    @JsonProperty("creationTime")
    private Calendar creationTime;

}
