package ChatApp.UserDomain.Service;

import ChatApp.UserDomain.Entity.User;
import ChatApp.UserDomain.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ChatUserService {

    @Autowired
    UserRepository repository;

    public List<User> getByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids))
            throw new IllegalArgumentException("Failed to query chat users (Reason: invalid ids).");
        return this.repository.findByIdIn(ids);
    }

    public User getById(Long id) {
        Assert.isTrue(Objects.nonNull(id), "Failed to query chat user (Reason: invalid id).");
        Optional<User> optionalUser = this.repository.findById(id);
        return optionalUser.orElse(null);
    }


}
