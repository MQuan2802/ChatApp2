package ChatApp.ConversationDomain.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FetchConversationRequest {
    List<Long> conversationIds;
    String name;
    List<Long> creatorIds;
    List<Long> participantUserIds;
    String phone;
    Sort.Direction sortDir = Sort.Direction.DESC;
    Boolean privateChat;

    String sortProperty = "lastMessageTime";

    int page = 0;
    int pageSize = 100;
}
