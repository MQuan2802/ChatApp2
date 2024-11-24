package ChatApp.UserDomain.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
public class FriendShip extends BaseEntity  {

    @ManyToOne
    @JoinColumn(name = "requested_user")
    @JsonSerialize(using = User.ToDto.class)
    User requestUser;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    @JsonSerialize(using = User.ToDto.class)
    User friend;

    public enum Status {
        PENDING,
        ACTIVE,
        USER_BLOCKED,
        FRIEND_BLOCKED,
        UN_FRIENDED
    }


}
