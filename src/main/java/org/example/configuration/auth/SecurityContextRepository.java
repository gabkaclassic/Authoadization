package org.example.configuration.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final AuthenticationManager manager;
    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new IllegalStateException("Method not supported");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {

        var header = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if(header == null || !header.startsWith("Bearer "))
            return Mono.empty();

        var auth = header.substring(7);

        var token = new UsernamePasswordAuthenticationToken(auth, auth);

        return manager.authenticate(token).map(SecurityContextImpl::new);
    }
}
