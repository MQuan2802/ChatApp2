package ChatApp.UserDomain.Service;


import ChatApp.UserDomain.Entity.SpecificationsBuilder;
import ChatApp.UserDomain.Entity.FriendShip;
import ChatApp.UserDomain.Entity.User;
import ChatApp.UserDomain.Entity.FriendShipSpecs_;
import ChatApp.UserDomain.Repository.FriendShipRepository;
import ChatApp.UserDomain.Repository.UserRepository;
import ChatApp.UserDomain.Request.FetchFriendRequest;
import com.google.common.collect.Iterables;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ChatApp.UserDomain.Entity.FriendShip.Status.UN_FRIENDED;

@Service
public class FriendShipService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendShipRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        ;
    }

    @Transactional
    public void addFriend(Long requestUserId, Long recipientUserId) {
        User requestUser = this.userRepository.findById(requestUserId).orElse(null);
        User recipientUser = this.userRepository.findById(recipientUserId).orElse(null);
        Assert.isTrue(Objects.nonNull(requestUser), "Failed to send add friend request (Reason: can not find request user)");
        Assert.isTrue(Objects.nonNull(recipientUser), "Failed to send add friend request (Reason: can not find recipient user)");

        FriendShip currfriendShip = this.repository.getFriendShip(Arrays.asList(requestUser.getId(), recipientUser.getId())).orElse(null);
        Assert.isTrue(Objects.isNull(currfriendShip), "Failed to send add friend request (Reason: friendship already existed)");

        FriendShip friendShip = FriendShip.builder()
                .status(FriendShip.Status.PENDING)
                .requestUser(requestUser)
                .friend(recipientUser)
                .build();

        this.repository.saveAndFlush(friendShip);
    }

    @Transactional
    public void updateFriendShipStatus(Long requestUserId, Long friendId, FriendShip.Status status) {
        Assert.isTrue(Objects.nonNull(requestUserId), "Failed to update friendship status (Reason: invalid request user)");
        Assert.isTrue(Objects.nonNull(friendId), "Failed to send update friendship status (Reason: invalid friend user)");
        FriendShip friendShip = this.repository.getFriendShip(Arrays.asList(requestUserId, friendId)).orElse(null);
        Assert.isTrue(Objects.nonNull(friendShip), "Failed to send update friendship status (Reason: can not find suitable friend)");
        if (status == FriendShip.Status.ACTIVE && ((friendShip.getStatus() == FriendShip.Status.USER_BLOCKED && friendShip.getRequestUser().getId() != requestUserId)
                || (friendShip.getStatus() == FriendShip.Status.FRIEND_BLOCKED && friendShip.getFriend().getId() != requestUserId)))
            throw new IllegalArgumentException("Failed to update friendship status (Reason: friend blocked user)");
        Assert.isTrue(friendShip.getStatus() == UN_FRIENDED, "Unfriended");
        friendShip.setStatus(status);
        this.repository.save(friendShip);
    }

    @Transactional
    public void updateFriendShipStatusByFriendShipId(Long requestUserId, Long friendShipId, FriendShip.Status status) {
        Assert.isTrue(Objects.nonNull(requestUserId), "Failed to update friendship status (Reason: invalid request user)");
        Assert.isTrue(Objects.nonNull(friendShipId), "Failed to send update friendship status (Reason: invalid friendShipId)");
        FriendShip friendShip = this.repository.findById(friendShipId).orElse(null);
        Assert.isTrue(Objects.nonNull(friendShip), "Failed to send update friendship status (Reason: can not find suitable friend)");
        if (status == FriendShip.Status.ACTIVE && ((friendShip.getStatus() == FriendShip.Status.USER_BLOCKED && friendShip.getRequestUser().getId() != requestUserId)
                || (friendShip.getStatus() == FriendShip.Status.FRIEND_BLOCKED && friendShip.getFriend().getId() != requestUserId)))
            throw new IllegalArgumentException("Failed to update friendship status (Reason: friend blocked user)");
        Assert.isTrue(friendShip.getStatus() == UN_FRIENDED, "Unfriended");
        friendShip.setStatus(status);
        this.repository.save(friendShip);
    }

    public Iterable<User> queryWithSpecification(FetchFriendRequest request) {
//        User requestUser = this.userRepository.findById(userId).orElse(null);
        SpecificationsBuilder<FriendShip> specificationsBuilder = new SpecificationsBuilder();
        specificationsBuilder.addSpecification(FriendShipSpecs_.filterByUserId(request.getUserId()));
        specificationsBuilder.addSpecification(FriendShipSpecs_.filterByFriendName(request.getName()));
        Iterable<FriendShip> friendShips = this.repository.findAll(specificationsBuilder.build(), new PageRequest(request.getPage(), request.getPageSize()));
        if (IterableUtils.isEmpty(friendShips))
            return new PageImpl<>(new ArrayList<>(), new PageRequest(request.getPage(), request.getPageSize()), Iterables.size(friendShips));
        List<User> friends = StreamSupport.stream(friendShips.spliterator(), false)
                .map(friendShip -> {
                    return friendShip.getRequestUser().getId().equals(request.getUserId()) ? friendShip.getFriend() : friendShip.getRequestUser();
                })
                .distinct()
                .collect(Collectors.toList());
        return new PageImpl<User>(friends,
                new PageRequest(request.getPage(), request.getPageSize()),
                Iterables.size(friendShips));
    }
}
