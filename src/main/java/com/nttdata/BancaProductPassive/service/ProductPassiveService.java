package com.nttdata.BancaProductPassive.service;

import com.nttdata.BancaProductPassive.domain.ProductPassive;
import com.nttdata.BancaProductPassive.domain.Client;
import com.nttdata.BancaProductPassive.repository.ProductPassiveRepository;

import com.nttdata.BancaProductPassive.web.mapper.ProductPassiveMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductPassiveService {

    @Autowired
    private ProductPassiveRepository productPassiveRepository;
    @Autowired
    private ProductPassiveMapper productPassiveMapper;

    private final String BASE_URL = "http://localhost:9040";

    TcpClient tcpClient = TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300)
            .doOnConnected(connection ->
                    connection.addHandlerLast(new ReadTimeoutHandler(3))
                            .addHandlerLast(new WriteTimeoutHandler(3)));

    private final WebClient client = WebClient.builder()
            .baseUrl("http://localhost:9040")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
            .build();

    public Mono<Client> findBydni(String dni){
        return this.client.get().uri("http://localhost:9040/v1/client/findByDni/{dni}", dni)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(Client.class);
    }

    public Flux<ProductPassive> findAll(){
        log.debug("findAll executed");
        return productPassiveRepository.findAll();
    }

    public Mono<ProductPassive> findById(String productpasiveId){
        log.debug("findById executed {}", productpasiveId);
        return productPassiveRepository.findById(productpasiveId);
    }

    public Mono<ProductPassive> create(ProductPassive productPassive){
        log.debug("create executed {}", productPassive);

        Mono<Client> client = findBydni(productPassive.getIdentityNumber());
        log.debug("findBydni executed {}" , client);
        log.info("findBydni executed {}" , client);
        System.out.println("client " + client);
        Mono<ProductPassive> product = productPassiveRepository.findByIdentityNumber(productPassive.getIdentityNumber());

       return client.switchIfEmpty(Mono.error(new Exception("Client Not Found" + productPassive.getIdentityNumber())))
               .flatMap(client1 -> {
                   if(client1.getTypeClient().equals("Personal")){
                       return product
                               .flatMap(product1 ->{
                                   if(product1.getTypeAccount().equals("Cuenta de Ahorro") || product1.getTypeAccount().equals("Cuenta Corriente")){
                                       return Mono.error(new Exception("No puede tener mas de una cuenta de AHORRO o CORRIENTE:  " + product1.getTypeAccount()));
                                   }
                                   else{
                                       return productPassiveRepository.save(productPassive);
                                   }
                               }).switchIfEmpty(productPassiveRepository.save(productPassive));
                   }
                   else{
                       if(productPassive.getTypeAccount().equals("Cuenta Corriente")){
                           return productPassiveRepository.save(productPassive);
                       }else{
                           return Mono.error(new Exception("No puede tener una cuenta de AHORRO o PLAZO FIJO:  " + productPassive.getTypeAccount()));
                       }

                   }
                   });
    }


    public Mono<ProductPassive> update(String productpasiveId,  ProductPassive productPassive){
        log.debug("update executed {}:{}", productpasiveId, productPassive);
        return productPassiveRepository.findById(productpasiveId)
                .flatMap(dbProductoPassive -> {
                    productPassiveMapper.update(dbProductoPassive, productPassive);
                    return productPassiveRepository.save(dbProductoPassive);
                });
    }

    public Mono<ProductPassive> delete(String productpasiveId){
        log.debug("delete executed {}", productpasiveId);
        return productPassiveRepository.findById(productpasiveId)
                .flatMap(existingProductPassive -> productPassiveRepository.delete(existingProductPassive)
                        .then(Mono.just(existingProductPassive)));
    }

    public Mono<ProductPassive> findByAccount(String account){
        log.debug("findByIdentityAccount executed {}" , account);
        return productPassiveRepository.findByAccount(account);
    }

    public Mono<ProductPassive> findByTypeAccountAndIdentityNumber(String typeAccount, String dni){
        log.debug("findByTypeAccountAndDocument executed {}" , typeAccount, dni);
        return productPassiveRepository.findByTypeAccountAndIdentityNumber(typeAccount, dni);
    }

}
