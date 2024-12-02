package ChatApp.UserDomain.Service;


import ChatApp.FileExchangeDomain.Service.S3Service;
import ChatApp.UserDomain.Entity.FriendShip;
import ChatApp.UserDomain.Entity.SpecificationsBuilder;
import ChatApp.UserDomain.Entity.User;
import ChatApp.UserDomain.Entity.UserSpecs;
import ChatApp.UserDomain.Repository.FriendShipRepository;
import ChatApp.UserDomain.Repository.UserRepository;
import ChatApp.UserDomain.Request.FetchUserRequest;
import ChatApp.UserDomain.Request.SignUpRequest;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    @Lazy(false)
    private UserRepository userRepository;

    @Autowired
    @Lazy(false)
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FriendShipRepository friendShipRepository;

    @Autowired
    private S3Service s3Service;

    @Transactional
    public User create(SignUpRequest request) {
        if (userRepository.countDuplicateEmail(request.getEmail()) > 0)
            throw new IllegalArgumentException("Failed to sign up (Reason: duplicate email)");
        if (userRepository.countDuplicatePhone(request.getUsername()) > 0)
            throw new IllegalArgumentException("Failed to sign up (Reason: duplicate phone)");

        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setDateOfBirth(request.getDateOfBrith());
        user.setPassword(passwordEncoder.encode("password"));
        return this.userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Kiểm tra xem User có tồn tại trong database không?
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(user);
    }

    // JWTAuthenticationFilter sẽ sử dụng hàm này
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );

        return new CustomUserDetails(user);
    }

//    @Autowired
//    UserRepository repository;

    public List<User> getByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids))
            throw new IllegalArgumentException("Failed to query chat users (Reason: invalid ids).");
        return this.userRepository.findByIdIn(ids);
    }

    public User getById(Long id) {
        Assert.isTrue(Objects.nonNull(id), "Failed to query chat user (Reason: invalid id).");
        return this.userRepository.findById(id).orElse(null);
    }

    public List<BigInteger> getUserIdsInConversation(Long conversationId) {
        Assert.isTrue(Objects.nonNull(conversationId), "Failed to query conversation user ids (Reason: invalid conversation id).");
        return this.userRepository.getUserIdsInConversation(conversationId);
    }

    public Iterable<User> queryWithSpecification(FetchUserRequest request) {
        SpecificationsBuilder<User> specificationsBuilder = new SpecificationsBuilder<>();
        specificationsBuilder.addSpecification(UserSpecs.filterByUserId(request.getUserId()));
        specificationsBuilder.addSpecification(UserSpecs.filterByUserPhone(request.getPhone()));
        Iterable<User> result = this.userRepository.findAll(specificationsBuilder.build(),new PageRequest(request.getPage(), request.getPageSize()));
        result.forEach(user -> {
            FriendShip.Status status = this.friendShipRepository.getFriendStatus(Arrays.asList(request.getRequestedUserId(), user.getId())).orElse(FriendShip.Status.UN_FRIENDED);
            user.setFriendStatus(status);
        });
        return result;
    }

    @Transactional
    public void updateInfo(String email, String phone, String password, Long userId) {
        if (StringUtils.isAllBlank(email, phone, password))
            throw new IllegalArgumentException("Failed to update user info (Reason: all data is empty)");
        User user = this.userRepository.findById(userId).orElse(null);
        if (Objects.isNull(user))
            throw new IllegalArgumentException("Failed to update user info (Reason: can not find user)");
        if (StringUtils.isNotBlank(password))
            user.setPassword(this.passwordEncoder.encode(password));
        if (StringUtils.isNotBlank(phone))
            user.setUsername(phone);
        if (StringUtils.isNotBlank(email))
            user.setEmail(email);
        this.userRepository.save(user);
    }

    @SneakyThrows
    @Transactional
    public String updateAvatar(MultipartFile file, Long userId, String extension) {
        User user = this.userRepository.findById(userId).orElse(null);
        if (Objects.isNull(user))
            throw new IllegalArgumentException("Failed to update profile photo (Reason: can not find user)");
        if (Objects.nonNull(user.getProfilePhoto())) {
            String fullPath = user.getProfilePhoto();
            String[] splitPath = fullPath.split("/");
            System.out.println("S3 file delete path : "+ String.format("%s/%s", S3Service.FileType.PROFILE.getLabel(), splitPath[splitPath.length - 1]));
             this.s3Service.deleteS3File(String.format("%s/%s", S3Service.FileType.PROFILE.getLabel(), splitPath[splitPath.length - 1]));
        }

        String imgUploadName = UUID.randomUUID().toString();
        File imgFile = new File(imgUploadName);
        FileUtils.writeByteArrayToFile(imgFile, file.getBytes());
        String s3Link = this.s3Service.uploadFile(imgFile, imgUploadName, S3Service.FileType.PROFILE, extension);
        user.setProfilePhoto(s3Link);
        this.userRepository.save(user);
        return s3Link;
    }


//    @Transactional
//    public void acceptFriend(long requestUserId, long recipientUserId) {
//        this.userRepository.updateFriendStatus(requestUserId, recipientUserId, FriendShip.Status.ACCEPTED.toString());
//        this.userRepository.updateFriendStatus(recipientUserId, requestUserId, FriendShip.Status.ACCEPTED.toString());
//    }
}