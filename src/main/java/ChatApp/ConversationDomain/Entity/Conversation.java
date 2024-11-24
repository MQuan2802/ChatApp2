package ChatApp.ConversationDomain.Entity;

import ChatApp.UserDomain.Entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

@Entity
@Getter
@Setter
@Where(clause = "status <> 'ARCHIVED'")
public class Conversation extends BaseEntity {

    @Column(name = "Admin")
    private Long admin;

    @OneToMany(mappedBy = "conversation", cascade = {CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Where(clause = "id IN (SELECT id FROM chat_message WHERE (true) ORDER BY creation_time DESC LIMIT 30)")
    List<ChatMessage> chatMessages;

    @OneToMany(mappedBy = "conversation")
    List<Participant> participants;

    @Column(name = "last_message_time")
    Calendar lastMessageTime;

    @Column
    @Enumerated(EnumType.STRING)
    private State status = State.ACTIVE;
    public enum State {
        ACTIVE,
        ARCHIVED,
    }
}
