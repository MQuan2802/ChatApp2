package ChatApp.UserDomain.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FetchUserRequest {

    private Long requestedUserId;
    private Long userId;
    private String phone;

    private int page = 0;
    private int pageSize = 100;
}
