package com.bilkent.devinsight.security;

import com.bilkent.devinsight.entity.User;
import com.bilkent.devinsight.exception.BaseException;
import com.bilkent.devinsight.exception.TokenException;
import com.bilkent.devinsight.exception.UserNotFoundException;
import com.bilkent.devinsight.repository.UserRepository;
import com.bilkent.devinsight.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final List<String> EXCLUDE_URL_STARTS_WITH = Arrays.asList("/health", "/public", "/auth", "/documentation", "/swagger-ui");
    private final List<String> INCLUDE_URLS = Arrays.asList("/auth/logout", "/auth/change-password");
    private final String EMPTY_URL = "/";

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
//    private final CacheService cacheService;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException, BaseException {
        try {
            if (!jwtTokenUtil.hasAuthorizationBearer(request))
                throw new TokenException("No authorization header is present", HttpStatus.UNAUTHORIZED);

            String accessToken = getToken(request);

            if (!jwtTokenUtil.validateAccessToken(accessToken))
                throw new TokenException("Unauthorized", HttpStatus.UNAUTHORIZED);

//            if (cacheService.containsKey("blacklist:" + accessToken))
//                throw new TokenException("Unauthorized", HttpStatus.UNAUTHORIZED);

            String username = jwtTokenUtil.extractAccessUsername(accessToken);

            Optional<User> optionalUser = userRepository.findByUsername(username);

            if (optionalUser.isEmpty())
                throw new UserNotFoundException(HttpStatus.UNAUTHORIZED);

            User user = optionalUser.get();

            request.setAttribute("user", user);
            request.setAttribute("accessToken", accessToken);
            filterChain.doFilter(request, response);
        } catch (BaseException exception) {
            this.handleException(response, exception);
        }
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        return  INCLUDE_URLS.stream().noneMatch(include -> request.getServletPath().equals(include))
                && EXCLUDE_URL_STARTS_WITH.stream().anyMatch(exclude -> request.getServletPath().startsWith(exclude)
                    || request.getServletPath().equals(EMPTY_URL));
    }


    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        String[] res = header.split(" ");

        if (res.length == 1) return "";

        return header.split(" ")[1].trim();
    }

    // This is the correct way to handle filter exceptions
    private void handleException(HttpServletResponse response, BaseException exception) throws IOException {
        response.setContentType("application/json");
        response.setStatus(exception.getHttpStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.builder()
                .operationResult(ApiResponse.OperationResult.builder()
                        .returnCode(Integer.toString(exception.getHttpStatus().value()))
                        .returnMessage(exception.getMessage())
                        .build())
                .build()));
    }

}
