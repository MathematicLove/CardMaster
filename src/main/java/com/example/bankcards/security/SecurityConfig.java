package com.example.bankcards.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;


import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    @Value("${app.security.cors.allowed-origins:*}")
    private List<String> allowedOrigins;

    public SecurityConfig(
        JwtAuthenticationFilter jwtFilter,
        @Qualifier("customUserDetailsService") UserDetailsService userDetailsService
) {
    this.jwtFilter = jwtFilter;
    this.userDetailsService = userDetailsService;
}


@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // Swagger только с ролью SWAGGER (Basic)
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").hasRole("SWAGGER")
                    // Публичные ручки
                    .requestMatchers("/api/auth/**").permitAll()
                    // Остальное — по JWT
                    .requestMatchers(HttpMethod.GET, "/api/cards/**").authenticated()
                    .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults()) // <— вот это добавили
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}

@Bean
public UserDetailsService swaggerUserDetails(
        @Value("${spring.security.user.name}") String username,
        @Value("${spring.security.user.password}") String password,
        PasswordEncoder encoder
) {
    var user = User.withUsername(username)
            .password(encoder.encode(password))
            .roles("SWAGGER")
            .build();
    return new InMemoryUserDetailsManager(user);
}

@Bean
public AuthenticationManager authenticationManager(
        PasswordEncoder encoder,
        @Qualifier("customUserDetailsService") UserDetailsService customUserDetailsService,
        @Qualifier("swaggerUserDetails") UserDetailsService swaggerUserDetails
) {
    DaoAuthenticationProvider appProvider = new DaoAuthenticationProvider();
    appProvider.setPasswordEncoder(encoder);
    appProvider.setUserDetailsService(customUserDetailsService);

    DaoAuthenticationProvider swaggerProvider = new DaoAuthenticationProvider();
    swaggerProvider.setPasswordEncoder(encoder);
    swaggerProvider.setUserDetailsService(swaggerUserDetails);

    return new ProviderManager(List.of(swaggerProvider, appProvider));
}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(allowedOrigins);
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
