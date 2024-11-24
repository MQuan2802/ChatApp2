package ChatApp.UserDomain.Entity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(FriendShip.class)
public class FriendShip_ extends BaseEntity_ {

//    public static volatile SingularAttribute<FriendShip, Long>  id;
    public static volatile SingularAttribute<FriendShip, User> requestUser;
    public static volatile SingularAttribute<FriendShip, FriendShip.Status> status;
    public static volatile SingularAttribute<FriendShip, User> friend;
}
