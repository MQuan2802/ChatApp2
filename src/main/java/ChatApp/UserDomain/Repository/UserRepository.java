package ChatApp.UserDomain.Repository;

import ChatApp.ConversationDomain.Entity.Conversation;
import ChatApp.UserDomain.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends
        CrudRepository<User, Long>, JpaRepository<User, Long>,
        JpaSpecificationExecutor<User>,
        Serializable {

    List<User> findByIdIn(Collection<Long> ids);

//    User findById(long id);

    User findByUsername(String username);

    Optional<User> findById(Long id);

    @Query(value = "SELECT user_id FROM participant WHERE conversation_id = (:conversationId)", nativeQuery = true)
    List<BigInteger> getUserIdsInConversation(@Param("conversationId") Long conversationId);

    @Query(value = "SELECT count(phone_number) FROM chat_user where phone_number = (:phone)", nativeQuery = true)
    Long countDuplicatePhone(@Param("phone") String phone);

    @Query(value = "SELECT count(email) FROM chat_user where email = (:email)", nativeQuery = true)
    Long countDuplicateEmail(@Param("email") String email);

//    @Query(value = "UPDATE user_friends SET status = (:status) WHERE user_id = (:userId) AND friend_id = (:friendId)", nativeQuery = true)
//    @Modifying
//    void updateFriendStatus(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("status") String status);
}
