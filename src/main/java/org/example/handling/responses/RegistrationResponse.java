package org.example.handling.responses;

import java.util.List;


public record RegistrationResponse(List<String> violations) {
}
