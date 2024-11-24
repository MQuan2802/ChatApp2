package ChatApp.ConversationDomain.Entity;


import ChatApp.UserDomain.Entity.BaseEntity;
import ChatApp.UserDomain.Entity.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = ChatMessage.ToDto.class)
public class ChatMessage extends BaseEntity {

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "sender", nullable = false)
    private Long sender;

    @Column(name = "content_type")
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    public enum ContentType {
        INIT_SESSION,
        ADD_PARTICIPANT,
        REMOVE_PARTICIPANT,
        TEXT,
        DOCUMENT,
        IMAGE,
        VIDEO
    }

    public static class ToDto extends JsonSerializer<ChatMessage> {

        @Override
        public void serialize(ChatMessage t, JsonGenerator jsonGenerator, SerializerProvider serializers)
                throws IOException, JsonProcessingException {
            if (null == t) {
                jsonGenerator.writeNull();
            } else {
//                jsonGenerator.writeNumber(t.getId());
//
//                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a");
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("content", t.getContent());
                jsonGenerator.writeStringField("contentType", t.getContentType().toString());
                jsonGenerator.writeNumberField("creationTime", t.getCreationTime().getTimeInMillis());
                jsonGenerator.writeNumberField("conversationId", t.getConversation().getId());
                jsonGenerator.writeNumberField("sender", t.getSender());
                jsonGenerator.writeNumberField("id", t.getId());
                jsonGenerator.writeEndObject();
            }
        }
    }

}
