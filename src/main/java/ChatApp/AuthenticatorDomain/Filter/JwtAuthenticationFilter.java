package ChatApp.AuthenticatorDomain.Filter;

import ChatApp.UserDomain.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Copyright 2019 {@author Loda} (https://loda.me).
 * This project is licensed under the MIT license.
 *
 * @since 5/1/2019
 * Github: https://github.com/loda-kun
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Autowired
    @Lazy(false)
    private JwtTokenProvider tokenProvider;

    @Autowired
    @Lazy(false)
    private UserService customUserDetailsService;

    @PostConstruct
    public void init(){
        if (Objects.isNull(tokenProvider))
            tokenProvider = new JwtTokenProvider();
        if (Objects.isNull(customUserDetailsService))
            customUserDetailsService = new UserService();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            logger.info("[{}][{}]:{}", getClass(), "JWTToken", jwt);

            if (Objects.isNull(tokenProvider))
                logger.info("token provider is null");

            if (StringUtils.isNotBlank(jwt) && tokenProvider.validateToken(jwt.split(";")[0])) {
                jwt = jwt.split(";")[0];
                Long userId = tokenProvider.getUserIdFromJWT(jwt);
                logger.info("[{}][{}]:{}", getClass(), "userId", userId);

                UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                logger.info("[{}][{}]:{}", getClass(), "userDetails", userDetails);

                if(userDetails != null) {
                    UsernamePasswordAuthenticationToken
                            authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                                                                                     userDetails
                                                                                             .getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            log.error("failed on set User authentication", ex);
            ex.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Cookie");
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
        return bearerToken;
    }
}
