package org.example.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableReactiveMongoRepositories
public class WebConfiguration {

    private final ReactiveAuthenticationManager manager;
    private final SecurityContextRepository repository;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity security) throws Exception {

        security.csrf().disable()
                .exceptionHandling()
                .accessDeniedHandler((exchange, denied) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                .authenticationEntryPoint((exchange, ex) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                .and().formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(manager)
                .securityContextRepository(repository)
                .authorizeExchange()
                .pathMatchers("/", "/auth/login", "/auth/registration", "/auth/confirm/**", "/test")
                .permitAll().anyExchange().authenticated();

        return security.build();
    }

    @Bean
    public RestTemplate restTemplate() {

        return new RestTemplate();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder(16);
    }
}
