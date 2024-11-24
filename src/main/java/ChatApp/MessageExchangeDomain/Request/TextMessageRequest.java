package ChatApp.MessageExchangeDomain.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextMessageRequest {
    private String content;
    private Long sender;
    private Long conversationId;
    private List<Long> recipients;
}
