package org.bank.ssalguerof.msvc.customerproducts.models.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

public interface TransactionYankiService {
    public Mono<Boolean> processMessage(String event) throws JsonProcessingException;
}
