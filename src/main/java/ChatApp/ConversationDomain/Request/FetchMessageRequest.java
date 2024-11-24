package ChatApp.ConversationDomain.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.Calendar;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FetchMessageRequest {
    List<Long> messageIds;

    Long conversationId;

    List<Long> senderIds;

    Sort.Direction sortDir = Sort.Direction.DESC;

    String sortProperty = "creationTime";

    Calendar startTs;

    Calendar endTs;

    int page = 0;

    int pageSize = 100;
}
