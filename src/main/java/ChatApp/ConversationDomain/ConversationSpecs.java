package ChatApp.ConversationDomain;

import ChatApp.ConversationDomain.Entity.Conversation;
import ChatApp.ConversationDomain.Entity.Conversation_;
import ChatApp.ConversationDomain.Entity.Participant;
import ChatApp.ConversationDomain.Entity.Participant_;
import ChatApp.UserDomain.Entity.User_;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ConversationSpecs {

    public static Specification<Conversation> filterByIds(final Iterable<Long> ids) {
        return (root, query, criteriaBuilder) -> {
            if (IterableUtils.isEmpty(ids)) {
                return criteriaBuilder.conjunction();
            }
            Set<Long> validIds = StreamSupport.stream(ids.spliterator(), false)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (validIds.isEmpty()) {
                return criteriaBuilder.disjunction();
            }
            query.distinct(true);
            return root.get(Conversation_.id).in(ids);
        };
    }

    public static Specification<Conversation> filterByCreatorIds(final Iterable<Long> ids) {
        return (root, query, criteriaBuilder) -> {
            if (IterableUtils.isEmpty(ids)) {
                return criteriaBuilder.conjunction();
            }
            Set<Long> validIds = StreamSupport.stream(ids.spliterator(), false)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (validIds.isEmpty()) {
                return criteriaBuilder.disjunction();
            }
            query.distinct(true);
            return root.get(Conversation_.admin).in(ids);
        };
    }

    public static Specification<Conversation> filterByStatus(final Iterable<Conversation.State> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (IterableUtils.isEmpty(statuses)) {
                return criteriaBuilder.conjunction();
            }
            Set<Conversation.State> validIds = StreamSupport.stream(statuses.spliterator(), false)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (validIds.isEmpty()) {
                return criteriaBuilder.disjunction();
            }
            return root.get(Conversation_.status).in(statuses);
        };
    }

    public static Specification<Conversation> filterByParticipantId(Long participantUserId) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(participantUserId)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join(Conversation_.participants).get(User_.id), participantUserId);
        };
    }

    public static Specification<Conversation> filterByConversationName(String name, Long participantUserId) {
        return (root, query, criteriaBuilder) -> {
            if(StringUtils.isBlank(name) || Objects.isNull(participantUserId))
                return criteriaBuilder.conjunction();
            System.out.print("ahihi"+ Conversation_.participants);
            Join<Conversation, Participant> listJoin = root.join(Conversation_.participants);
            return criteriaBuilder.and(
                    criteriaBuilder.like(listJoin.get(Participant_.conversationDisplayName), "%" + name.toLowerCase() + "%"),
                    criteriaBuilder.equal(listJoin.get(Participant_.user).get(User_.id), participantUserId)
            );
        };
    }

}
