package org.example.controllers.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RegistrationResponse {

    private List<String> violations;

}
