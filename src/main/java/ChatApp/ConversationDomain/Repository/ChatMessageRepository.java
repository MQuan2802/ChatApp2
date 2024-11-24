package ChatApp.ConversationDomain.Repository;

import ChatApp.ConversationDomain.Entity.ChatMessage;
import org.apache.logging.log4j.message.Message;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends
        CrudRepository<ChatMessage, Long>,
        JpaSpecificationExecutor<ChatMessage>,
        Serializable {
    Optional<ChatMessage> findById(Long id);
}
