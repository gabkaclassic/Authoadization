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

    @GetMapping
    public Mono<ResponseEntity<String>> login(@RequestParam String login, @RequestParam String password) {

        return accountService.login(login, password);
    }
    @PostMapping("/registration")
    public Mono<ResponseEntity<List<String>>> registration(@RequestParam String login, @RequestParam String password, @RequestParam String email) throws InterruptedException {

        return accountService.registry(login, password, email);
    }

    @GetMapping("/confirm/{code}")
    public Mono<ResponseEntity<String>> confirmation(@PathVariable String code) {

        return accountService.confirm(code);
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
