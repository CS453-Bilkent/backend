package com.bilkent.devinsight.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class ApplicationSecurity implements WebMvcConfigurer {

    private final JwtTokenFilter jwtTokenFilter;
    private final RoleHandlerInterceptor roleHandlerInterceptor;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${springdoc.user.name}")
    private String springdocUsername;

    @Value("${springdoc.user.password}")
    private String springdocPassword;

    private static final String[] SWAGGER_BLACKLIST = {
            "/documentation",
            "/swagger-ui/**"
    };

    @Bean
    public FilterRegistrationBean<JwtTokenFilter> jwtTokenFilterFilterRegistrationBean() {
        FilterRegistrationBean<JwtTokenFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(jwtTokenFilter);
        registrationBean.addUrlPatterns("*"); // we use shouldNotFilter on JwtTokenFilter class
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);

        return registrationBean;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().configurationSource(request -> {
            CorsConfiguration cors = new CorsConfiguration()
                    .applyPermitDefaultValues()
                    .setAllowedOriginPatterns(List.of("http://localhost:3000/", frontendUrl));

            cors.addAllowedMethod("DELETE");
            return cors;
        });

        http.csrf().disable();

        http.authorizeRequests()
                .requestMatchers(SWAGGER_BLACKLIST).authenticated().and().httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User
                .withUsername(springdocUsername)
                .password(encoder().encode(springdocPassword))
                .roles("SWAGGER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleHandlerInterceptor);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
    }
}