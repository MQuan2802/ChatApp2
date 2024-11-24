package ChatApp.ConversationDomain.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationCreateRequest {
    private Long Admin;
    private List<Long> participants;
    private String name;
}
