package org.example.accounts;

import lombok.RequiredArgsConstructor;
import org.example.configuration.utils.JwtUtil;
import org.example.configuration.utils.MailUtil;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService implements ReactiveUserDetailsService {

    private static final String SUCCESSFUL_CONFIRM = "Successful";
    private static final String UNSUCCESSFUL_CONFIRM = "Unsuccessful";

    private final AccountRepository repository;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder encoder;

    private final ReactiveMongoTemplate template;

    private final MailUtil mailUtil;

    private static final Random random = new Random();

    private static final ResponseEntity<Object> UNAUTHORIZED = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();


    @Override
    public Mono<UserDetails> findByUsername(String login) {
        return repository.findByLogin(login);
    }

    public void registry(String login, String password) throws InterruptedException {

        var account = new Account();
        account.setLogin(login);
        account.setPassword(password);
        account.setCode(randomConfirmationCode());
        repository.save(account).subscribe();

        mailUtil.addToMessageQueue(account);
    }

    public Mono<ResponseEntity> login(String login, String password) {


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

    public Flux<Account> all() {
        return repository.findAll();
    }
}
