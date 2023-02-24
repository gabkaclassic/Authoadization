package org.example.accounts;

import lombok.RequiredArgsConstructor;
import org.example.configuration.utils.AccountValidator;
import org.example.configuration.utils.JwtUtil;
import org.example.configuration.utils.MailUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService implements ReactiveUserDetailsService {

    private static final String SUCCESSFUL_CONFIRM = "Successful";
    private static final String UNSUCCESSFUL_CONFIRM = "Unsuccessful";

    private final AccountRepository repository;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder encoder;

    private final AccountValidator validator;

    private final MailUtil mailUtil;

    private static final Random random = new Random();

    private static final ResponseEntity<String> UNAUTHORIZED = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");


    @Override
    public Mono<UserDetails> findByUsername(String login) {
        return repository.findByLogin(login);
    }

    public Mono<ResponseEntity<List<String>>> registry(String login, String password)  {


        return repository
                .existsByLogin(login)
                .map(exists -> {

                    var violations = new ArrayList<String>();

                    if(exists)
                        violations.add("Account with this email already exists");
                    else
                        validator.validate(login, password, violations);

                    if(!violations.isEmpty())
                        return ResponseEntity.badRequest().body(violations);

                    var account = new Account();
                    account.setLogin(login);
                    account.setPassword(password);
                    account.setCode(randomConfirmationCode());
                    save(account);

                    try {
                        mailUtil.addToMessageQueue(account);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    return ResponseEntity.ok(violations);
        });

    }

    public Mono<ResponseEntity<String>> login(String login, String password) {


        return findByUsername(login).cast(Account.class)
                .map(
                        account -> (encoder.matches(password, account.getPassword()) && account.isAccountNonLocked()) ?
                ResponseEntity.ok(jwtUtil.generateToken(account))
                        : UNAUTHORIZED
                );
    }

    public Mono<ResponseEntity<String>> confirm(String code) {

        return repository.findByCode(code).map(account -> {

            if(account == null)
                return ResponseEntity.ok().body(UNSUCCESSFUL_CONFIRM);

            account.setCode(null);

            return ResponseEntity.ok(SUCCESSFUL_CONFIRM);
        });
    }

    private static String randomConfirmationCode() {

        return random.ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(8)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private void save(Account account) {
        repository.save(account).subscribe();
    }

    public Flux<Account> all() {
        return repository.findAll();
    }

    public Mono<ResponseEntity<List<String>>> update(String login, String password) {
        return repository
                .existsByLogin(login)
                .map(exists -> {

                    var violations = new ArrayList<String>();

                    if(!exists)
                        violations.add("Account with this email not exists");
                    else
                        validator.validate(login, password, violations);

                    if(!violations.isEmpty())
                        return ResponseEntity.badRequest().body(violations);

                    var account = new Account();
                    account.setLogin(login);
                    account.setPassword(password);
                    account.setCode(randomConfirmationCode());
                    save(account);

                    try {
                        mailUtil.addToMessageQueue(account);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    return ResponseEntity.ok(violations);
                });

    }
}
