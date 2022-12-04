package com.nttdata.BancaProductPassive.repository;

import com.nttdata.BancaProductPassive.domain.ProductPassive;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductPassiveRepository extends ReactiveMongoRepository<ProductPassive, String> {
    Mono<ProductPassive> findByAccount(String account);
    Mono<ProductPassive> findByTypeAccountAndIdentityNumber(String typeAccount, String dni);
    Mono<ProductPassive> findByIdentityNumber(String dni);

}
