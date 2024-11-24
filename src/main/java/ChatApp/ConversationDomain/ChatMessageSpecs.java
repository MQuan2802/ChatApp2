package ChatApp.ConversationDomain;

import ChatApp.ConversationDomain.Entity.ChatMessage;
import ChatApp.ConversationDomain.Entity.ChatMessage_;
import ChatApp.ConversationDomain.Entity.Conversation_;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.Calendar;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ChatMessageSpecs {

    public static Specification<ChatMessage> filterByIds(final Iterable<Long> ids) {
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
            return root.get(ChatMessage_.id).in(validIds);
        };
    }

    public static Specification<ChatMessage> filterBySenders(final Iterable<Long> senderIds) {
        return (root, query, criteriaBuilder) -> {
            if (IterableUtils.isEmpty(senderIds)) {
                return criteriaBuilder.conjunction();
            }
            Set<Long> validIds = StreamSupport.stream(senderIds.spliterator(), false)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (validIds.isEmpty()) {
                return criteriaBuilder.disjunction();
            }
            query.distinct(true);
            return root.get(ChatMessage_.sender).in(validIds);
        };
    }

    public static Specification<ChatMessage> filterByConversations(Long conversationId) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(conversationId)) {
                return criteriaBuilder.conjunction();
            }
//            Set<Long> validIds = StreamSupport.stream(conversationIds.spliterator(), false)
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toSet());
//            if (validIds.isEmpty()) {
//                return criteriaBuilder.disjunction();
//            }
            query.distinct(true);
            System.out.print("Conversation_id "+Conversation_.id);
            System.out.print(".conversation"+ChatMessage_.conversation);
            return criteriaBuilder.equal(root.get(ChatMessage_.conversation).get(Conversation_.id), conversationId);
        };
    }

    public static Specification<ChatMessage> filterByCreationTime(Calendar startTs, Calendar endTs) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(startTs) || Objects.isNull(endTs)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(
                    criteriaBuilder.lessThanOrEqualTo(root.get(ChatMessage_.creationTime), endTs),
                    criteriaBuilder.greaterThanOrEqualTo(root.get(ChatMessage_.creationTime), startTs));
        };
    }
}
