package org.example.accounts;

import lombok.RequiredArgsConstructor;
import org.example.security.SecurityData;
import org.example.utils.AccountValidator;
import org.example.utils.JwtUtil;
import org.example.utils.MailUtil;
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
import java.util.Map;
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

    private final SecurityData securityData;

    private final MailUtil mailUtil;

    private static final Random random = new Random();

    private static final ResponseEntity<String> UNAUTHORIZED = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");


    @Override
    public Mono<UserDetails> findByUsername(String login) {
        return repository.findByLogin(login);
    }

    public Mono<ResponseEntity<List<String>>> registry(String login, String password, String email)  {


        return repository
                .existsByLogin(login)
                .map(exists -> {

                    var violations = new ArrayList<String>();

                    if(exists)
                        violations.add("Account with this email already exists");
                    else
                        validator.validate(login, password, email, violations);

                    if(!violations.isEmpty())
                        return ResponseEntity.badRequest().body(violations);

                    var account = new Account();
                    account.setEmail(login);
                    account.setPassword(password);
                    account.setEmail(email);
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

    public Mono<ResponseEntity<List<String>>> update(
            String oldLogin,
            String newLogin,
            String oldPassword,
            String newPassword,
            String newEmail) {

        return repository
                .findByLogin(oldLogin).cast(Account.class).defaultIfEmpty(null)
                .map(account -> {

                    var violations = new ArrayList<String>();

                    if(newLogin == null && newPassword == null && newEmail == null)
                        violations.add("Nothing to update");

                    if(account == null)
                        violations.add("Account with this login not exists");
                    else if(!encoder.matches(oldPassword, account.getPassword()))
                        violations.add("Wrong password");
                    else {

                        if(newLogin != null)
                            validator.validLogin(newLogin, violations);
                        if(newPassword != null)
                            validator.validPassword(newPassword, violations);
                        if(newEmail != null)
                            validator.validEmail(newEmail, violations);
                    }


                    if(!violations.isEmpty())
                        return ResponseEntity.badRequest().body(violations);

                    if(newLogin != null)
                        account.setLogin(newLogin);
                    if(newPassword != null)
                        account.setPassword(newPassword);
                    if(newEmail != null)
                        account.setEmail(newEmail);

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

    public Mono<ResponseEntity<Map<String, String>>> interactionData(String login, String password) {

        return repository.findByLogin(login).cast(Account.class).defaultIfEmpty(null)
                .map(account -> {

                    if(account == null)
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", "Account with this login is not found"));
                    else if(encoder.matches(password, account.getPassword()))
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", "Wrong password"));

                    return ResponseEntity.ok(Map.of("id", account.getId(), "key", securityData.getInteractionKey()));
                });
    }
}
