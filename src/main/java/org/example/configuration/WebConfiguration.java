package org.example.configuration;

import lombok.RequiredArgsConstructor;
import org.example.data.accounts.AccountRepository;
import org.example.configuration.auth.SecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableReactiveMongoRepositories(basePackageClasses = AccountRepository.class)
public class WebConfiguration {

    private final ReactiveAuthenticationManager manager;
    private final SecurityContextRepository repository;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity security) throws Exception {

        security.cors().disable()
                .csrf().disable()
                .exceptionHandling()
                .accessDeniedHandler((exchange, denied) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                .authenticationEntryPoint((exchange, ex) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                .and().formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(manager)
                .securityContextRepository(repository)
                .authorizeExchange()
                .pathMatchers( "/auth/**", "/auth", "/security/interactionKey")
                .permitAll().anyExchange().authenticated();

        return security.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder(8);
    }
}
