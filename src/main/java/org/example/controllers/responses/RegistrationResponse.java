package org.example.controllers.responses;

import java.util.List;


public record RegistrationResponse(List<String> violations) {
}
