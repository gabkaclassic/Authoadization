package org.example.handling.controllers;

import org.example.data.accounts.AccountService;
import org.example.handling.responses.AuthorizationResponse;
import org.example.handling.responses.RegistrationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.util.List;


@CrossOrigin(allowedHeaders = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST}, allowCredentials = "true", originPatterns = "*")
@RequestMapping("/auth")
@RestController
@SuppressWarnings("unused")
public class AuthorizationController {

    private final AccountService accountService;

    private final String PEAGEON_URL;


    public AuthorizationController(@Autowired AccountService accountService, @Value("${peageon.url}") String peageonUrl) {
        this.accountService = accountService;
        PEAGEON_URL = peageonUrl;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<AuthorizationResponse>> login(@RequestParam String login, @RequestParam String password) {

        return accountService.login(login, password);
    }
    @PostMapping
    public Mono<ResponseEntity<RegistrationResponse>> registration(@RequestParam String login, @RequestParam String password, @RequestParam String email) throws InterruptedException {

        return accountService.registry(login, password, email);
    }

    @GetMapping("/confirm/{code}")
    public Mono<Void> confirmation(@PathVariable String code, ServerHttpResponse response, ServerHttpRequest request) throws IOException {

        accountService.confirm(code);
        var url = request.getHeaders().getFirst("referer");
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create(url == null ? PEAGEON_URL : url));

        return response.setComplete();
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<List<String>>> update(@RequestParam String oldLogin,
                                                     @RequestParam(required = false) String newLogin,
                                                     @RequestParam String oldPassword,
                                                     @RequestParam(required = false) String newPassword,
                                                     @RequestParam(required = false) String newEmail) {

        return accountService.update(oldLogin, newLogin, oldPassword, newPassword, newEmail);
    }
}
