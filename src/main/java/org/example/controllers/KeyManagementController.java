package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.exceptions.InvalidSecretKeyException;
import org.example.security.SecurityData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("security")
@RequiredArgsConstructor
public class KeyManagementController {

    private final SecurityData data;


    @PostMapping("/interactionKey")
    @ResponseStatus(HttpStatus.OK)
    public void setKey(@RequestParam("secretKey") String secret, @RequestParam("key") String key) throws InvalidSecretKeyException {

        data.setInteractionKey(secret, key);
    }
}
