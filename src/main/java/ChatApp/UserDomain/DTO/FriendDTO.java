package ChatApp.UserDomain.DTO;

import ChatApp.UserDomain.Entity.FriendShip;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@NoArgsConstructor
public class FriendDTO {
    @Column(name = "friend_id")
    private Long friendId;

    @Column(name = "friend_ship_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendShip.Status status;

    public FriendDTO(FriendShip friendShip) {
//        this.friendId = friendShip.getFriendId();
        this.status = friendShip.getStatus();
    }
}
