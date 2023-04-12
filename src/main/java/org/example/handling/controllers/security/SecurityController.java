package org.example.handling.controllers.security;

import lombok.RequiredArgsConstructor;
import org.example.handling.exceptions.InvalidSecretKeyException;
import org.example.data.security.SecurityData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("security")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityData data;
    @PostMapping("/interactionKey")
    public ResponseEntity setKey(@RequestParam("secretKey") String secret, @RequestParam("key") String key) throws InvalidSecretKeyException {

        return data.setInteractionKey(secret, key);
    }
}