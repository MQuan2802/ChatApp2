package ChatApp.UserDomain.Controller;

import ChatApp.UserDomain.DTO.FriendDTO;
import ChatApp.UserDomain.Entity.FriendShip;
import ChatApp.UserDomain.Entity.User;
import ChatApp.UserDomain.Request.FetchFriendRequest;
import ChatApp.UserDomain.Service.FriendShipService;
import ChatApp.UserDomain.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("api/friend")
public class FriendController {

    @Autowired
    UserService userService;

    @Autowired
    FriendShipService friendShipService;

    @RequestMapping(value = "request", method = RequestMethod.POST)
    public ResponseEntity requestFriend(@RequestParam("requestUser") Long requestUserId, @RequestParam("recipient") Long recipient) {
        this.friendShipService.addFriend(requestUserId, recipient);
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "update/status", method = RequestMethod.PUT)
    public ResponseEntity updateStatus(@RequestParam("requestUser") Long requestUserId,
                                       @RequestParam("status") FriendShip.Status status,
                                       @RequestParam(value = "friend", required = false) Long friendId,
                                       @RequestParam(value = "friendShip", required = false) Long friendShipId){

        Assert.isTrue(Objects.nonNull(friendId) || Objects.nonNull(friendShipId), "Failed to update friendship status (friend id or friendshipId is required)" );
        if (Objects.nonNull(friendId)) {
            this.friendShipService.updateFriendShipStatus(requestUserId, friendId, status);
        } else {
            this.friendShipService.updateFriendShipStatusByFriendShipId(requestUserId, friendShipId, status);
        }
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "fetch", method = RequestMethod.POST)
    public Iterable<User> fetchFriend(@RequestBody FetchFriendRequest request) {
        Assert.isTrue(request.getPage() > -1, "Failed to fetch friends (Reason: invalid page)");
        Assert.isTrue(request.getPageSize() > 0, "Failed to fetch friends (Reason: invalid page_size)");
        return this.friendShipService.queryWithSpecification(request);
    }


}
