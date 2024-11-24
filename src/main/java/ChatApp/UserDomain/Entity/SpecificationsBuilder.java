package ChatApp.UserDomain.Entity;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

public class SpecificationsBuilder<T> {

    private Specifications<T> specifications;
    private boolean isEmpty = true;

    public SpecificationsBuilder<T> addSpecification(Specification<T> specification) {
        if (null == specification)
            return this;
        if (isEmpty) {
            specifications = Specifications.where(specification);
            isEmpty = false;
        } else
            specifications = specifications.and(specification);
        return this;
    }

    public Specifications<T> build() {
        return specifications;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
