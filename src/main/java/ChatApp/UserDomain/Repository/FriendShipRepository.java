package ChatApp.UserDomain.Repository;

import ChatApp.UserDomain.Entity.FriendShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendShipRepository extends CrudRepository<FriendShip, Long>,
        JpaRepository<FriendShip, Long>,
        JpaSpecificationExecutor<FriendShip>,
        PagingAndSortingRepository<FriendShip, Long>
{

    @Query(value = "SELECT f FROM FriendShip f WHERE f.requestUser.id = (:userId) OR f.friend.id = (:userId)")
    List<FriendShip> fetchFriendShip(@Param("userId") Long userId);

    @Query(value = "SELECT f From FriendShip f WHERE f.requestUser.id IN (:userIds) AND f.friend.id IN (:userIds)")
    Optional<FriendShip> getFriendShip(@Param("userIds") List<Long> userIds);

    @Query(value = "SELECT f.status From FriendShip f WHERE f.requestUser.id IN (:userIds) AND f.friend.id IN (:userIds)")
    Optional<FriendShip.Status> getFriendStatus(@Param("userIds") List<Long> userIds);
}
