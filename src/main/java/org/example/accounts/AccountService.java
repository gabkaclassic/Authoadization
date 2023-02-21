package org.example.accounts;

import lombok.RequiredArgsConstructor;
import org.example.configuration.utils.JwtUtil;
import org.example.configuration.utils.MailUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService implements ReactiveUserDetailsService {

    private final AccountRepository repository;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder encoder;

    private final MailUtil mailUtil;

    private static final ResponseEntity<Object> UNAUTHORIZED = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();


    @Override
    public Mono<UserDetails> findByUsername(String login) {
        return repository.findByLogin(login);
    }

    public void registry(String login, String password) {

        var account = new Account();
        account.setLogin(login);
        account.setPassword(password);
        account.setCode(UUID.randomUUID().toString());
        repository.save(account);


    }

    public Mono<ResponseEntity> login(String login, String password) {


        return findByUsername(login).cast(Account.class)
                .map(
                        account -> (encoder.matches(password, account.getPassword()) && account.isAccountNonLocked()) ?
                ResponseEntity.ok(jwtUtil.generateToken(account))
                        : UNAUTHORIZED
                );
    }

    public Mono<ResponseEntity<Account>> confirm(String code) {

        return repository.findByCode(code).mapNotNull(account -> {
            account.setCode(null);
            return ResponseEntity.ok(account);
        });
    }
}
