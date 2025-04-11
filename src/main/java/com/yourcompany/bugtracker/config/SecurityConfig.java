package com.yourcompany.bugtracker.config;

import com.yourcompany.bugtracker.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll() // Allow static resources
                .requestMatchers("/login", "/register", "/error").permitAll() // Allow access to login/register
                .requestMatchers("/api/**").permitAll() // Permit API access for now (secure later if needed)
                .requestMatchers("/bugs/new", "/bugs/save").hasAnyRole("ADMIN", "DEVELOPER", "REPORTER") // Who can create
                .requestMatchers("/bugs/*/edit", "/bugs/*/update", "/bugs/*/assign").hasAnyRole("ADMIN", "DEVELOPER") // Who can edit/assign
                .requestMatchers("/admin/**").hasRole("ADMIN") // Admin specific section
                .anyRequest().authenticated() // All other requests need authentication
            )
            .formLogin(form -> form
                .loginPage("/login") // Custom login page URL
                .loginProcessingUrl("/login") // URL to submit username/password
                .defaultSuccessUrl("/home", true) // Redirect on successful login
                .failureUrl("/login?error=true") // Redirect on failed login
                .permitAll() // Allow everyone to see login page
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // Trigger logout on /logout
                .logoutSuccessUrl("/login?logout=true") // Redirect after logout
                .invalidateHttpSession(true) // Invalidate session
                .deleteCookies("JSESSIONID") // Delete cookies
                .permitAll()
            );
            // Add CSRF protection if needed (enabled by default, Thymeleaf handles it)
            // .csrf(csrf -> csrf.disable()) // Disable if using stateless API clients heavily

        return http.build();
    }

     // Expose AuthenticationManager (needed for programmatic login, potentially tests)
      // Expose AuthenticationManager (needed for programmatic login, potentially tests)
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                    PasswordEncoder passwordEncoder,
                                                    CustomUserDetailsService userDetailService) throws Exception {
     return http.getSharedObject(AuthenticationManagerBuilder.class)
           .userDetailsService(userDetailService)
           .passwordEncoder(passwordEncoder)
           .and() // <--- Deprecated part
           .build();
 }
}