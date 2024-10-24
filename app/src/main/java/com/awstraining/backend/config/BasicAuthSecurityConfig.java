package com.awstraining.backend.config;

import com.awstraining.backend.users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static com.awstraining.backend.users.Users.USER;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Configuration of basic security features like turning on the HTTP basic authentication.
 * <p>
 * application.yml allows in standard configuration (e.g. spring.security.user) only one user-password mapping
 * so that this class uses InmemoryUserDetailsManager in combination with application.yml. That way multiple
 * user-passwords can be configured.
 */
@Configuration
public class BasicAuthSecurityConfig {
    private final Users users;
    /**
     * Contains configured users to be mapped
     */
    @Autowired
    public BasicAuthSecurityConfig(final Users users) {
        this.users = users;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers();
    }

    @Bean
    public SecurityFilterChain basicAuthFilterChain(final HttpSecurity http) throws Exception {
        http
                .csrf().disable()                // NOSONAR
                // disable csrf to enable POST, PUT, DELETE requests
                // https://docs.spring.io/spring-security/site/docs/current/reference/html/features.html#csrf-when
                .authorizeRequests()
                .antMatchers("/device/**").hasAnyRole(USER)
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .sessionManagement().sessionCreationPolicy(STATELESS);

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        users.getUsers()
                .forEach(user -> manager.createUser(User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRoles().toArray(String[]::new))
                        .build()));

        return manager;
    }

    /**
     * Returns a password encoder which is used for encoding passwords here.
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * helper method for generating bcrypt encoded passwords.
     */
    public static void main(final String[] args) {
        final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        final String welt = bCryptPasswordEncoder.encode("putYourPasswordHere");
        System.out.println(welt);
    }
}
