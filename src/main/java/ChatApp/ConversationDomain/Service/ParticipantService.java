package ChatApp.ConversationDomain.Service;

import ChatApp.ConversationDomain.Entity.Conversation;
import ChatApp.ConversationDomain.Entity.Participant;
import ChatApp.ConversationDomain.Repository.ConversationRepository;
import ChatApp.ConversationDomain.Repository.ParticipantRepository;
import ChatApp.UserDomain.Entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Service
public class ParticipantService {
    @Autowired
    ParticipantRepository repository;

    @Autowired
    ConversationRepository conversationRepository;

    public Participant create(Conversation conversation, User user, String conversationDisplayName) {
        Participant participant = new Participant();
        participant.setConversation(conversation);
        participant.setUser(user);
        participant.setConversationDisplayName(conversationDisplayName);
        return this.repository.save(participant);
    }

    @SneakyThrows
    @Transactional
    public void removeParticipant(Long userId, Long conversationId) {
        Conversation conversation = this.conversationRepository.findById(conversationId).orElse(null);
        Assert.isTrue(Objects.nonNull(conversation), "Failed to remove participant (Reason: cannot find conversation)");
        Assert.isTrue(conversation.getParticipants().size() > 1, "Failed to remove participant (Reason: cannot remove all the participants)");
        this.repository.removeParticipant(userId, conversationId);
    }

    @Transactional
    public void updateLastView(Long participantUserId, List<Long> conversationIds) {
        List<Participant> participants = this.repository.findByConversationIdsAndUserId(participantUserId, conversationIds);
        participants.forEach(participant -> participant.setLastView(Calendar.getInstance()));
        this.repository.saveAll(participants);
    }
}
