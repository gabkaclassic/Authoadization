package org.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SuppressWarnings("unused")
public class HomepageController {

    @GetMapping
    public String homepage() {

        return "index";
    }
}
