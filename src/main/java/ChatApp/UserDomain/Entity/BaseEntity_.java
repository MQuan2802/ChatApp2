package ChatApp.UserDomain.Entity;

import ChatApp.UserDomain.Entity.BaseEntity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Calendar;

@StaticMetamodel(BaseEntity.class)
public class BaseEntity_ {
    public static volatile SingularAttribute<BaseEntity, Long> id;
    public static volatile SingularAttribute<BaseEntity, Calendar> creationTime;

}
