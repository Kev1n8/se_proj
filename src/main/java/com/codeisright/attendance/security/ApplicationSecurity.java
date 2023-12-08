package com.codeisright.attendance.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ApplicationSecurity extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final UserDetailsService userDetailsService;

    @Autowired
    public ApplicationSecurity(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);

//        provider.setPasswordEncoder(new BCryptPasswordEncoder());

        provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() {
        return authentication -> authenticationProvider().authenticate(authentication);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //TODO: make sure the security and the Controllers working together
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register**").permitAll()
                        .requestMatchers("/logout").permitAll()
                        .requestMatchers("/usr/{id}/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/usr/{id}/student/**").hasRole("STUDENT")
                        .anyRequest().authenticated()
                ).csrf(CsrfConfigurer::disable);
        return http.build();
    }
}