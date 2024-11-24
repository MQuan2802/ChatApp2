package ChatApp.ConversationDomain.Entity;

import ChatApp.ConversationDomain.Entity.ChatMessage;
import ChatApp.ConversationDomain.Entity.Conversation;
import ChatApp.UserDomain.Entity.BaseEntity_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Calendar;

@StaticMetamodel(ChatMessage.class)
public class ChatMessage_ extends BaseEntity_ {

//    public static volatile SingularAttribute<ChatMessage, Long> id;
    public static volatile SingularAttribute<ChatMessage, Calendar> creationTime;
    public static volatile SingularAttribute<ChatMessage, Long> sender;
    public static volatile SingularAttribute<ChatMessage, Conversation> conversation;

}
