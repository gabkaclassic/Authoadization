package org.example.handling.controllers;

import lombok.RequiredArgsConstructor;
import org.example.data.accounts.Account;
import org.example.data.accounts.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final AccountService service;

    @GetMapping
    public Flux<Account> test() {

        return service.all();
    }
}
