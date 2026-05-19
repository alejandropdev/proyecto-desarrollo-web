package com.muk.security;

import com.muk.security.jwt.JwtEntryPoint;
import com.muk.security.jwt.JwtProvider;
import com.muk.security.jwt.JwtTokenFilter;
import com.muk.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtEntryPoint jwtEntryPoint;

    @Bean
    public JwtTokenFilter jwtTokenFilter(JwtProvider jwtProvider, AuthCookieService authCookieService) {
        return new JwtTokenFilter(jwtProvider, userDetailsService, authCookieService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenFilter jwtTokenFilter) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(jwtEntryPoint))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        // Public endpoints (adjust as needed for the app)
                        .requestMatchers(HttpMethod.GET, "/api/menu/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/platos/**").permitAll()
                        .requestMatchers("/api/productos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/clientes").permitAll()
                        
                        // Roles
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/operadores/**").hasRole("OPERADOR")
                        
                        // Other endpoints require authentication
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll() // like static files, angular routes
                );

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
