package ChatApp.ConversationDomain;

import ChatApp.ConversationDomain.Entity.Conversation;
import ChatApp.ConversationDomain.Entity.Conversation_;
import ChatApp.ConversationDomain.Entity.Participant;
import ChatApp.ConversationDomain.Entity.Participant_;
import ChatApp.UserDomain.Entity.User;
import ChatApp.UserDomain.Entity.User_;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.List;
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
            query.distinct(true);
            return root.get(Conversation_.status).in(statuses);
        };
    }

    @SneakyThrows
    public static Specification<Conversation> filterByParticipantIdsV1(List<Long> participantUserIds) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(participantUserIds)) {
                return criteriaBuilder.conjunction();
            }
            try {
                System.out.println("filter with Participant user Id: "+ new ObjectMapper().writeValueAsString(participantUserIds));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            query.distinct(true);
//            return criteriaBuilder.equal(root.join(Conversation_.participants).get(Participant_.user).get(User_.id), participantUserId);
            return root.join(Conversation_.participants).get(Participant_.user).get(User_.id).in(participantUserIds);

        };
    }

    @SneakyThrows
    public static Specification<Conversation> filterByParticipantIdsV3(List<Long> participantUserIds) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(participantUserIds)) {
                return criteriaBuilder.conjunction();
            }
            try {
                System.out.println("filter with Participant user Id: "+ new ObjectMapper().writeValueAsString(participantUserIds));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            query.distinct(true);
//            return criteriaBuilder.equal(root.join(Conversation_.participants).get(Participant_.user).get(User_.id), participantUserId);
            Predicate finalPredicate = null;

            for (Long participantUserId : participantUserIds) {
                Join<Conversation, Participant> participantsJoin = root.join(Conversation_.participants);
                Join<Participant, User> userJoin = participantsJoin.join(Participant_.user);
                Predicate userPredicate = criteriaBuilder.equal(userJoin.get(User_.id), participantUserId);

                if (finalPredicate == null) {
                    finalPredicate = userPredicate;
                } else {
                    finalPredicate = criteriaBuilder.and(finalPredicate, userPredicate);
                }
            }
            return finalPredicate;
        };
    }

    @SneakyThrows
    public static Specification<Conversation> filterByParticipantIdV2(Long participantUserId) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(participantUserId)) {
                return criteriaBuilder.conjunction();
            }
            try {
                System.out.println("filter with Participant user Id: "+ new ObjectMapper().writeValueAsString(participantUserId));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            query.distinct(true);
            return criteriaBuilder.equal(root.join(Conversation_.participants).get(Participant_.user).get(User_.id), participantUserId);
//            return root.join(Conversation_.participants).get(Participant_.user).get(User_.id).in(participantUserId);

        };
    }

    public static Specification<Conversation> filterByParticipantPhone(String phone) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.isBlank(phone)) {
                return criteriaBuilder.conjunction();
            }
            query.distinct(true);
            return criteriaBuilder.like(root.join(Conversation_.participants).get(Participant_.user).get(User_.username), "%" + phone.toLowerCase() + "%");
        };
    }

    public static Specification<Conversation> filterByPrivateChat(Boolean privateChat) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(privateChat))
                return criteriaBuilder.conjunction();
            query.distinct(true);
            return criteriaBuilder.equal(root.get(Conversation_.privateChat), privateChat);
        };
    }

    public static Specification<Conversation> filterByConversationName(String name, Long participantUserId) {
        return (root, query, criteriaBuilder) -> {
            if(StringUtils.isBlank(name) || Objects.isNull(participantUserId))
                return criteriaBuilder.conjunction();
            Join<Conversation, Participant> listJoin = root.join(Conversation_.participants);
            return criteriaBuilder.and(
                    criteriaBuilder.like(listJoin.get(Participant_.conversationDisplayName), "%" + name.toLowerCase() + "%"),
                    criteriaBuilder.equal(listJoin.get(Participant_.user).get(User_.id), participantUserId)
            );
        };
    }


    public static Specification<Conversation> filterByConversationName(String name) {
        return (root, query, criteriaBuilder) -> {
            if(StringUtils.isBlank(name))
                return criteriaBuilder.conjunction();
            Join<Conversation, Participant> listJoin = root.join(Conversation_.participants);
            return
                    criteriaBuilder.like(listJoin.get(Participant_.conversationDisplayName), "%" + name.toLowerCase() + "%");


        };
    }
}
