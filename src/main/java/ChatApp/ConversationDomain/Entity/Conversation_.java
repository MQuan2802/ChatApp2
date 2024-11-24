package ChatApp.ConversationDomain.Entity;

import ChatApp.UserDomain.Entity.BaseEntity_;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Calendar;

@StaticMetamodel(Conversation.class)
public class Conversation_ extends BaseEntity_ {

    public static volatile SingularAttribute<Conversation, Long> admin;
    public static volatile SingularAttribute<Conversation, Conversation.State> status;
    public static volatile ListAttribute<Conversation, Participant> participants;
    public static volatile SingularAttribute<Conversation, Calendar> lastMessageTime;

}
