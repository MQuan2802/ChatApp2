package ChatApp.UserDomain.Controller;

import ChatApp.AuthenticatorDomain.Filter.JwtTokenProvider;
import ChatApp.MailService;
import ChatApp.MessageExchangeDomain.SMS.SMSService;
import ChatApp.UserDomain.Entity.User;
import ChatApp.UserDomain.Request.FetchUserRequest;
import ChatApp.UserDomain.Request.LoginRequest;
import ChatApp.UserDomain.Request.SignUpRequest;
import ChatApp.UserDomain.Response.LoginResponse;
import ChatApp.UserDomain.Service.CustomUserDetails;
import ChatApp.UserDomain.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService emailService;

    @Autowired
    private SMSService smsService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Nếu không xảy ra exception tức là thông tin hợp lệ
        // Set thông tin authentication vào Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.print(String.format("is instance of User %s",((CustomUserDetails)authentication.getPrincipal()).getUser() instanceof User));
        // Trả về jwt cho người dùng.
        String jwt = tokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();
        return new LoginResponse(jwt, userId);
    }


    @RequestMapping(value = "signUp", method = RequestMethod.POST)
    public ResponseEntity signUp(@RequestBody SignUpRequest signUpRequest) {
        User user;
        try {
            user = this.userService.create(signUpRequest);
       } catch(Exception e) {
           throw new IllegalArgumentException(e.getLocalizedMessage());
       }
       Map<String, Object> response = new HashMap<>();
       response.put("id", user.getId());
       response.put("success", true);
       return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "send/mail/verify_code", method = RequestMethod.GET)
    public ResponseEntity emailVerifyCode(@RequestParam("email") @Email String mail) {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        String content =  "Hi chat user, \n\n"
                + "Your verify code is: "+ code
                + "\nThis is an automated email. Please do not reply"
                + "\n\nThank you, \nService team.";
        this.emailService.sendMessage("[CHAT APP VERIFY CODE]", content, mail);
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "send/phone/verify_code", method = RequestMethod.GET)
    public ResponseEntity phoneVerifyCode(@RequestParam("phone") String phone) throws IOException {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        System.out.println("code:" + this.smsService.sendSMS(phone, String.valueOf(code), 2, "0379309318"));
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("success", true);
        return ResponseEntity.ok(response);
    }


    @RequestMapping(value = "test/config", method = RequestMethod.GET)
    public String testConfig() {
        return "Ok";
    }

    @RequestMapping(value = "user/find", method = RequestMethod.POST)
    public Iterable<User> fetchUser(@RequestBody FetchUserRequest request) {
        return this.userService.queryWithSpecification(request);
    }

    @RequestMapping(value = "update/info", method = RequestMethod.PUT)
    public ResponseEntity updateInfo(@RequestParam(value = "email", required = false) @Email String email,
                                     @RequestParam("userId") @NonNull Long userId,
                                     @RequestParam(value = "phone", required = false) @NotBlank String phone,
                                     @RequestParam(value = "password", required = false) @NotBlank String password) {
        this.userService.updateInfo(email, phone, password, userId);
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "update/avatar", method = RequestMethod.POST)
    public ResponseEntity updateAvatar (@RequestParam("file") MultipartFile file,
                                        @RequestParam("userId") @NonNull Long userId) {
        this.userService.updateAvatar(file, userId);
        return ResponseEntity.ok("ok");
    }

}
