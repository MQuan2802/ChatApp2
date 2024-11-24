package ChatApp.UserDomain.Entity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class UserSpecs {

    public static Specification<User> filterByUserId(final Long id) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(id)) {
                return criteriaBuilder.conjunction();
            }
            query.distinct(true);
            return criteriaBuilder.equal(root.get(User_.id), id);
        };
    }

    public static Specification<User> filterByUserPhone(final String phone) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.isBlank(phone)) {
                return criteriaBuilder.conjunction();
            }
            query.distinct(true);
            return criteriaBuilder.like(root.get(User_.username), phone);
        };
    }
}
