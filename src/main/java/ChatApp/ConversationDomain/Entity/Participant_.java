package ChatApp.ConversationDomain.Entity;

import ChatApp.UserDomain.Entity.BaseEntity_;
import ChatApp.UserDomain.Entity.User;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Participant.class)
public class Participant_ extends BaseEntity_ {
    public static volatile SingularAttribute<Participant, String> name;
    public static volatile SingularAttribute<Participant, Long> conversation;
    public static volatile SingularAttribute<Participant, User> user;
    public static volatile SingularAttribute<Participant, String> conversationDisplayName;

}
