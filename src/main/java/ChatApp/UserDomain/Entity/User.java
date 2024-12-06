package ChatApp.UserDomain.Entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "chat_user")
@Getter
@Setter
@JsonSerialize(using = User.ToDto.class)
public class User extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "email")
    private String email;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Transient
    private FriendShip.Status friendStatus;

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User))
            return false;
        User user = (User) o;
        return this.id.equals(user.getId())
                && this.username.equals(user.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, id);
    }

    public static class ToDto<T extends User> extends JsonSerializer<User> {

        @Override
        public void serialize(User t, JsonGenerator jsonGenerator, SerializerProvider serializers)
                throws IOException, JsonProcessingException {
            if (null == t) {
                jsonGenerator.writeNull();
            } else {
//                jsonGenerator.writeNumber(t.getId());
//
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("UserName", t.getName());
                jsonGenerator.writeStringField("phoneNumber", t.getUsername());
                jsonGenerator.writeNumberField("id", t.getId());
                jsonGenerator.writeStringField("profilePhoto", t.getProfilePhoto());
                if (Objects.nonNull(t.getFriendStatus()))
                    jsonGenerator.writeStringField("friendStatus", t.getFriendStatus().toString());
                jsonGenerator.writeStringField("dateOfBirth", t.getDateOfBirth().toString());
                jsonGenerator.writeStringField("mail", t.getEmail());
                jsonGenerator.writeEndObject();
            }
        }
    }
}
