package ChatApp.UserDomain.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @NotEmpty(message = "Name must not be blank")
    private String name;

    @NotEmpty(message = "Phone must not be blank")
    @JsonProperty("phone")
    private String username;

    @NotEmpty(message = "Password must not be blank")
    private String password;

    @Valid
    private LocalDate dateOfBrith;

    @Email(message = "Email should be valid")
    private String email;
}
