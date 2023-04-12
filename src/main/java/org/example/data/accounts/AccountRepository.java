package org.example.data.accounts;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String> {

    Mono<UserDetails> findByLogin(String login);

    Mono<Account> findByCode(String code);

    Mono<Boolean> existsByLogin(String login);

    Mono<Boolean> existsByLoginOrEmail(String login, String email);
}
