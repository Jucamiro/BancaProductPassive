package com.nttdata.BancaProductPassive.web;

import com.nttdata.BancaProductPassive.domain.ProductPassive;
import com.nttdata.BancaProductPassive.service.ProductPassiveService;
import com.nttdata.BancaProductPassive.web.mapper.ProductPassiveMapper;
import com.nttdata.BancaProductPassive.web.model.ProductPassiveModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/productpassive")
public class ProductPassiveController {
    @Value("${spring.application.name}")
    String name;

    @Value("${server.port}")
    String port;

    WebClient webClient;
    @Autowired
    private ProductPassiveService productPassiveService;

    @Autowired
    private ProductPassiveMapper productPassiveMapper;

    @GetMapping("/GetAll")
    public Mono<ResponseEntity<Flux<ProductPassiveModel>>> getAll(){
        log.info("getAll executed");
        return Mono.just(ResponseEntity.ok()
                .body(productPassiveService.findAll()
                        .map(productPassive -> productPassiveMapper.entityToModel(productPassive))));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductPassiveModel>> getById(@PathVariable String id){
        log.info("getById executed {}", id);
        Mono<ProductPassive> response = productPassiveService.findById(id);
        return response
                .map(productPassive -> productPassiveMapper.entityToModel(productPassive))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<ProductPassiveModel>> create(@Valid @RequestBody ProductPassiveModel request){
        log.info("create executed {}", request);
        return productPassiveService.create(productPassiveMapper.modelToEntity(request))
                .map(productPassive -> productPassiveMapper.entityToModel(productPassive))
                .flatMap(c -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name, port, "productPassive", c.getIdProductPassive())))
                        .body(c)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductPassiveModel>> updateById(@PathVariable String id, @Valid @RequestBody ProductPassiveModel request){
        log.info("updateById executed {}:{}", id, request);
        return productPassiveService.update(id, productPassiveMapper.modelToEntity(request))
                .map(productPassive -> productPassiveMapper.entityToModel(productPassive))
                .flatMap(c -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name, port, "productPassive", c.getIdProductPassive())))
                        .body(c)))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id){
        log.info("deleteById executed {}", id);
        return productPassiveService.delete(id)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/findByAccount/{account}")
    public Mono<ResponseEntity<ProductPassiveModel>> findByAccount(@PathVariable String account){
        log.info("findByIdentityAccount executed {}", account);
        Mono<ProductPassive> response = productPassiveService.findByAccount(account);
        return response
                .map(productPassive -> productPassiveMapper.entityToModel(productPassive))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
