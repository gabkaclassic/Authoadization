package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.accounts.Account;
import org.example.accounts.AccountService;
import org.example.configuration.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AccountService accountService;

    @PostMapping("/login")
    public Mono<ResponseEntity> login(ServerWebExchange exchange) {

        return exchange.getFormData()
                .flatMap(credentials -> accountService.login(
                        credentials.getFirst("login"),
                        credentials.getFirst("password"))
                );
    }
    @PostMapping("/registration")
    public String registration(@RequestParam String login, @RequestParam String password) {

        accountService.registry(login, password);
        return "Successful";
    }

    @GetMapping("/confirm/{code}")
    public Mono<ResponseEntity<Account>> someString(@PathVariable String code) {

        return accountService.confirm(code);
    }
}
