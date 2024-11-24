package ChatApp.ConversationDomain.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationUpdateRequest {

    @NotBlank
    String name;

    @NonNull
    Long conversationId;
}
