package org.example.handling.filters;

import org.example.handling.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    private final List<PathPattern> pathPatterns;
    private static final Mono<ServerResponse> UNAUTHORIZED =
            ServerResponse.status(HttpStatus.UNAUTHORIZED).build();

    public AuthWebFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;

        pathPatterns = new ArrayList<>();
        pathPatterns.add(new PathPatternParser()
                .parse("/security/interactionKey"));
        pathPatterns.add(new PathPatternParser()
                .parse("/auth"));
        pathPatterns.add(new PathPatternParser()
                .parse("/auth/**"));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        var request = exchange.getRequest();
        var response = exchange.getResponse();

        var path = request.getPath();
        if(pathPatterns.stream().anyMatch(p -> p.matches(path)))
            return chain.filter(exchange);

        return exchange.getSession().flatMap(session -> {
            var token = (String) session.getAttribute(session.getId());

            if(token == null || token.isEmpty() || !jwtUtil.validateToken(token)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return Mono.empty();
            }

            return chain.filter(exchange);
        });
    }
}
