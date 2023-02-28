package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.accounts.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AuthorizationController {

    private final AccountService accountService;

    @PostMapping("/email")
    public Mono<ResponseEntity> login(ServerWebExchange exchange) {

        return exchange.getFormData()
                .flatMap(credentials -> accountService.login(
                        credentials.getFirst("email"),
                        credentials.getFirst("password"))
                );
    }
    @PostMapping("/registration")
    public Mono<ResponseEntity<List<String>>> registration(@RequestParam String login, @RequestParam String password) throws InterruptedException {

        return accountService.registry(login, password);
    }

    @GetMapping("/confirm/{code}")
    public Mono<ResponseEntity<String>> someString(@PathVariable String code) {

        return accountService.confirm(code);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<List<String>>> update(@RequestParam String login, @RequestParam String password) {

        return accountService.update(login, password);
    }
}
