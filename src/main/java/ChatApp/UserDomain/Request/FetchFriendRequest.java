package ChatApp.UserDomain.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FetchFriendRequest {

    @JsonProperty("requestedUserId")
    private Long userId;
    private List<Long> friendIds;
    private String name;

    private int page = 0;
    private int pageSize = 100;
}
