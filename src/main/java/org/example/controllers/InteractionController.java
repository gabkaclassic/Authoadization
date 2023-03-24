package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.accounts.AccountService;
import org.example.exceptions.InvalidSecretKeyException;
import org.example.security.SecurityData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("interaction")
@RequiredArgsConstructor
public class InteractionController {

    private final SecurityData data;

    private final AccountService accountService;


    @PostMapping("/key")
    public ResponseEntity setKey(@RequestParam("secretKey") String secret, @RequestParam("key") String key) throws InvalidSecretKeyException {

        return data.setInteractionKey(secret, key);
    }
}