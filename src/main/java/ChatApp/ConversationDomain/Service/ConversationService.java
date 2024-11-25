package ChatApp.ConversationDomain.Service;

import ChatApp.ConversationDomain.ConversationSpecs;
import ChatApp.ConversationDomain.Dto.ConversationDTO;
import ChatApp.ConversationDomain.Entity.Conversation;
import ChatApp.ConversationDomain.Entity.Participant;
import ChatApp.ConversationDomain.Repository.ConversationRepository;
import ChatApp.ConversationDomain.Repository.ParticipantRepository;
import ChatApp.ConversationDomain.Request.ConversationCreateRequest;
import ChatApp.ConversationDomain.Request.ConversationUpdateRequest;
import ChatApp.ConversationDomain.Request.FetchConversationRequest;
import ChatApp.UserDomain.Entity.SpecificationsBuilder;
import ChatApp.UserDomain.Entity.User;
import ChatApp.UserDomain.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    ConversationRepository repository;

    @Autowired
    ParticipantRepository participantRepository;

    @Autowired
    UserService chatUserService;

    @Autowired
    ParticipantService participantService;

    @Autowired
    EntityManager entityManager;

    public List<ConversationDTO> fetchConversationByUserIdAfterDate(long userId, LocalDate startDate) {
        return this.repository.getConversationByUserIdAfterDate(userId, startDate)
                .stream().map(ConversationDTO::new).collect(Collectors.toList());

    }

    @Transactional
    public ConversationDTO create(ConversationCreateRequest request) {
        if (CollectionUtils.isEmpty(request.getParticipants()))
            throw new IllegalArgumentException("Failed to create conversation (Reason: invalid participants).");
        List<User> users = this.chatUserService.getByIds(request.getParticipants());

        if (CollectionUtils.isEmpty(request.getParticipants()))
            throw new IllegalArgumentException("Failed to create conversation (Reason: invalid users).");

        Conversation conversation = new Conversation();
        conversation.setAdmin(request.getAdmin());
        conversation.setLastMessageTime(Calendar.getInstance());
        if (users.size() < 3)
            conversation.setPrivateChat(true);
//        conversation.setName(StringUtils.isBlank(request.getName()) ?  request.getName() :
//                StringUtils.join(users.stream().map(user -> user.getName()).collect(Collectors.toList()), ','));
        Conversation savedConversation = this.repository.save(conversation);
        Map<Long, String> conversationNames = this.generateConversationDisplayNameMap(users, request.getAdmin());

        List<Participant> savedParticipants = users.stream()
                .map(user -> this.participantService.create(savedConversation, user, conversationNames.get(user.getId()))).collect(Collectors.toList());
        savedConversation.setParticipants(savedParticipants);

        return new ConversationDTO(savedConversation);
    }


    public Map<Long, String> generateConversationDisplayNameMap(List<User> users, Long admin) {
        if (CollectionUtils.isEmpty(users))
            return new HashMap<>();
        if (users.size() == 2) {
            Map<Long, String> result = new HashMap<>();
            result.put(users.get(0).getId(), users.get(1).getName());
            result.put(users.get(1).getId(), users.get(0).getName());
            return result;
        }

        Map<Long, String> result = new HashMap<>();
        String conversationName = String.format("%s, %s...", admin, users.stream().filter(user -> !user.getId().equals(admin)).findFirst().orElse(null));
        users.forEach(user -> result.put(user.getId(), conversationName));
        return result;
    }

    @Transactional
    public void update(ConversationUpdateRequest request) {
        Conversation conversation = this.repository.findById(request.getConversationId()).orElse(null);
        if (Objects.isNull(conversation))
            throw new IllegalArgumentException("Failed to update conversation (Reason: can not find conversation)");
        List<Participant> participants = conversation.getParticipants();
        if (participants.size() < 3)
            throw new IllegalArgumentException("Failed to update conversation name (Reason: cannot update conversation name in private chat)");
        participants.forEach(participant -> participant.setConversationDisplayName(request.getName()));
        this.participantRepository.saveAll(participants);
    }

    @Transactional
    public Long addConversationParticipant(Long userId, Long conversationId) {
        Assert.isTrue(Objects.nonNull(userId), "Failed to add participant to conversation (Reason: invalid userId).");
        Assert.isTrue(Objects.nonNull(conversationId), "Failed to add participant to conversation  (Reason: invalid conversationId).");

        User user = this.chatUserService.getById(userId);
        Conversation conversation = this.repository.findById(conversationId).orElse(null);

        Assert.isTrue(Objects.nonNull(user), "Failed to add participant to conversation (Reason: cannot find user).");
        Assert.isTrue(Objects.nonNull(conversation), "Failed to add participant to conversation (Reason: cannot find conversation).");

        Participant savedParticipant = this.participantService.create(conversation, user, conversation.getParticipants().get(0).getConversationDisplayName());
        conversation.getParticipants().add(savedParticipant);
        return savedParticipant.getId();
    }

    @Transactional
    public void archiveConversation(Long conversationId) {
        Assert.isTrue(Objects.nonNull(conversationId), "Failed to archive conversation (Reason: invalid conversationId).");
        Conversation conversation = this.repository.findById(conversationId).orElse(null);
        if (Objects.nonNull(conversation)) {
            conversation.setStatus(Conversation.State.ARCHIVED);
            this.repository.save(conversation);
        }

    }

    public Iterable<Conversation> queryWithSpecification(FetchConversationRequest request) {
        SpecificationsBuilder<Conversation> specificationsBuilder = new SpecificationsBuilder();
        specificationsBuilder.addSpecification(ConversationSpecs.filterByIds(request.getConversationIds()));
        specificationsBuilder.addSpecification(ConversationSpecs.filterByCreatorIds(request.getCreatorIds()));
        specificationsBuilder.addSpecification(ConversationSpecs.filterByParticipantId(request.getParticipantUserId()));
        specificationsBuilder.addSpecification(ConversationSpecs.filterByConversationName(request.getName(), request.getParticipantUserId()));
//        specificationsBuilder.addSpecification(ConversationSpecs.filterByStatus(states));
        return this.repository.findAll(specificationsBuilder.build(),new PageRequest(request.getPage(), request.getPageSize(), request.getSortDir(), request.getSortProperty()));
    }
}
