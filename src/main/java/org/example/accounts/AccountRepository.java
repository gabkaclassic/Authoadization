package org.example.accounts;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, Long> {

    Mono<UserDetails> findByLogin(String login);

    Mono<Account> findByCode(String code);
}
