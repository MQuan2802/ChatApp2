package ChatApp.UserDomain.Entity;

import ChatApp.UserDomain.Entity.BaseEntity_;
import ChatApp.UserDomain.Entity.User;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(User.class)
public class User_ extends BaseEntity_ {
//    public static volatile SingularAttribute<User, Long> id;
    public static volatile SingularAttribute<User, String> name;
    public static volatile SingularAttribute<User, String> username;
}
