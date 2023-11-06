package org.example.handling.controllers.authoadization;
import org.example.data.accounts.Account;
import org.example.data.accounts.AccountService;
import org.example.handling.responses.AuthorizationResponse;
import org.example.handling.responses.RegistrationResponse;
import org.example.handling.utils.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureWebTestClient
public class AuthorizationControllerTest {

    @Autowired
    private AuthorizationController authorizationController;

    @MockBean
    private AccountService accountService;
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${url.peageon}")
    private String redirectUrl;

    @BeforeEach
    void setup() {

    }

    @Test
    public void testLogin() {
        Account account = Account.builder()
                .login("test")
                .password("pass")
                .build();
        String token = jwtUtil.generateToken(account);
        AuthorizationResponse response = new AuthorizationResponse(token);
        given(accountService.login(anyString(), anyString())).willReturn(Mono.just(ResponseEntity.ok().body(response)));

        webTestClient.get()
                .uri("/auth?login=test&password=pass")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isEqualTo(token);
    }

    @Test
    public void testInvalidLoginOnRegistration() {
        RegistrationResponse response = new RegistrationResponse(Collections.emptyList());
        given(accountService.registry(anyString(), anyString(), anyString())).willReturn(Mono.just(ResponseEntity.ok().body(response)));

        webTestClient.post()
                .uri("/auth?login=t&password=pass&email=test@example.com")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()

                .jsonPath("$.violations", Matchers.hasItem(Matchers.matchesRegex("The login can contain \\d+-\\d+ characters and 1 letter\n")));
    }
    @Test
    public void testInvalidPasswordOnRegistration() {
        RegistrationResponse response = new RegistrationResponse(Collections.emptyList());
        given(accountService.registry(anyString(), anyString(), anyString())).willReturn(Mono.just(ResponseEntity.ok().body(response)));

        webTestClient.post()
                .uri("/auth?login=test&password=pass&email=test@example.com")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.violations").isArray()
                .jsonPath("$.violations", Matchers.hasItem(Matchers.matchesRegex("The password must contain \\d+-\\d+characters, of which 1 digit and 1 specified character")));
    }
    @Test
    public void testInvalidEmailOnRegistration() {
        RegistrationResponse response = new RegistrationResponse(Collections.emptyList());
        given(accountService.registry(anyString(), anyString(), anyString())).willReturn(Mono.just(ResponseEntity.ok().body(response)));
