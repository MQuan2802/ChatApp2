package ChatApp.ConversationDomain.Entity;

import ChatApp.UserDomain.Entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table
@Where(clause = "status <> 'ARCHIVED'")
public class Conversation extends BaseEntity {

    @Column(name = "Admin")
    private Long admin;

    @OneToMany(mappedBy = "conversation", cascade = {CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Where(clause = "id IN (SELECT id FROM chat_message WHERE (true) ORDER BY creation_time DESC LIMIT 30)")
    List<ChatMessage> chatMessages;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    List<Participant> participants;

    @Column(name = "last_message_time")
    Calendar lastMessageTime;

    @Column(name = "is_private_chat")
    boolean privateChat;

    @Column
    @Enumerated(EnumType.STRING)
    private State status = State.ACTIVE;
    public enum State {
        ACTIVE,
        ARCHIVED,
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Conversation))
            return false;
        Conversation conv = (Conversation) o;
        return this.id.equals(conv.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
