package ChatApp.ConversationDomain.Repository;

import ChatApp.ConversationDomain.Entity.Participant;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public interface ParticipantRepository extends
        CrudRepository<Participant, Long>,
        JpaSpecificationExecutor<Participant>,
        Serializable {

    @Query(value = "DELETE FROM participant WHERE id = (:participantId) AND conversation_id = (:conversationId);", nativeQuery = true)
    @Modifying
    void removeParticipant(@Param("participantId") Long participantId, @Param("conversationId") Long conversationId);

    @Modifying
    @Query(value = "SELECT P FROM Participant p JOIN p.user u JOIN p.conversation c WHERE u.id = (:userId) AND c.id IN (:conversationIds)")
    List<Participant> findByConversationIdsAndUserId(@Param("userId") Long userId, @Param("conversationIds") List<Long> conversationIds);

}
