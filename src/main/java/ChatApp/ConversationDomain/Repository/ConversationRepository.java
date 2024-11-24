package ChatApp.ConversationDomain.Repository;

import ChatApp.ConversationDomain.Entity.Conversation;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends
        CrudRepository<Conversation, Long>,
        JpaSpecificationExecutor<Conversation>,
        Serializable {

//    @Query(value = "SELECT m, conv FROM message m " +
//            "INNER JOIN conversation conv ON m.conversation_id = conv.id " +
//            "INNER JOIN group_member gr ON m.conversation_id = gr.conversation_id " +
//            "WHERE m.creation_time >= (:filterDate) " +
//            "AND gr.user_id = (:userId)", nativeQuery = true)
//    List<Object[]> getMessageByUserIdAfterDate(@Param("userId") long userId, @Param("filterDate") LocalDate filter);

    Optional<Conversation> findById(Long conversationId);

    @Query(value = "SELECT m.conversation FROM ChatMessage m " +
            "WHERE m.creationTime >= (:filterDate) " +
            "AND m.conversation.id IN (SELECT p.conversation.id FROM Participant p WHERE p.user.id = (:userId))")
    List<Conversation> getConversationByUserIdAfterDate(@Param("userId") long userId, @Param("filterDate") LocalDate filter);

}

