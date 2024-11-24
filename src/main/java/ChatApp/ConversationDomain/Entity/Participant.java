package ChatApp.ConversationDomain.Entity;

import ChatApp.UserDomain.Entity.BaseEntity;
import ChatApp.UserDomain.Entity.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
@Getter
@Setter
@JsonSerialize(using = Participant.ToDto.class)
public class Participant extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @OneToOne
    @JsonSerialize(using = User.ToDto.class)
    private User user;

    @Column(name = "last_view")
    private Calendar lastView;

    @Column(name = "conversation_display_name")
    private String conversationDisplayName;

    public static class ToDto extends JsonSerializer<Participant> {

        @Override
        public void serialize(Participant t, JsonGenerator jsonGenerator, SerializerProvider serializers)
                throws IOException, JsonProcessingException {
            if (null == t) {
                jsonGenerator.writeNull();
            } else {
//                jsonGenerator.writeNumber(t.getId());
//
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a");
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("conversationDisplayName", t.getConversationDisplayName());
                jsonGenerator.writeNumberField("userId", t.user.getId());
                jsonGenerator.writeNumberField("conversationId", t.conversation.getId());
                jsonGenerator.writeStringField("participantName", t.user.getName());
                jsonGenerator.writeNumberField("lastView", t.getLastView().getTimeInMillis());
                jsonGenerator.writeNumberField("id", t.getId());
                jsonGenerator.writeEndObject();
            }
        }
    }
}
