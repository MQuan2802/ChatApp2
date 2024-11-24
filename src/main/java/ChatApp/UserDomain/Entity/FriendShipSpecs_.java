package ChatApp.UserDomain.Entity;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

public class FriendShipSpecs_ {

    public static Specification<FriendShip> filterByUserId(final Long id) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(id)) {
                return criteriaBuilder.conjunction();
            }
            query.distinct(true);
            return criteriaBuilder.or(criteriaBuilder.equal(root.get(FriendShip_.friend).get(User_.id), id),
                    criteriaBuilder.equal(root.get(FriendShip_.requestUser).get(User_.id), id));
        };
    }

    public static Specification<FriendShip> filterByStatus(final List<Long> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (IterableUtils.isEmpty(statuses)) {
                return criteriaBuilder.conjunction();
            }
            return root.get(FriendShip_.status).in(statuses);
        };
    }

    public static Specification<FriendShip> filterByFriendName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.isBlank(name)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(FriendShip_.friend).get(User_.name)), "%" + name.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(FriendShip_.requestUser).get(User_.name)), "%" + name.toLowerCase() + "%"));
        };
    }


//    public static Specification<FriendShip> filterUpdatedBy(Long updatedBy) {
//        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(FriendShip_.updatedBy), updatedBy);
//    }

}
