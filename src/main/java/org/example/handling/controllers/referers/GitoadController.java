package org.example.handling.controllers.referers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Consumer;

@CrossOrigin(allowedHeaders = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST}, allowCredentials = "true", originPatterns = "*")
@RestController
@RequestMapping("/gitoad")
public class GitoadController {

    private final String GITOAD_BASE_URL;

    public GitoadController(@Value("${url.gitoad}") String gitoadBaseUrl) {
        GITOAD_BASE_URL = gitoadBaseUrl;
    }
    @GetMapping(value = "**")
    public Mono<String> test(ServerHttpResponse response, ServerHttpRequest request, WebSession session) {

        return WebClient.builder()
                .baseUrl(GITOAD_BASE_URL)
                .defaultCookies(getCookies(request))
                .defaultHeaders(headers -> {
                    headers.addAll(request.getHeaders());
                    headers.add("Auth-token", session.getAttribute(session.getId()));
                })
                .build()
                .get()
                .uri(URI.create(request.getPath().subPath(7).value()))
                .retrieve().bodyToMono(String.class);
    }

    private static Consumer<MultiValueMap<String, String>> getCookies(ServerHttpRequest request) {

        return cookies -> request.getCookies()
                .forEach((name, values) ->
                        cookies.addAll(name, values.stream()
                                .map(HttpCookie::getValue)
                                .toList()
                        )
                );
    }
}
