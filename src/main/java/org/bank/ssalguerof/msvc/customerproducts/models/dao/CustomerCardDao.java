package org.bank.ssalguerof.msvc.customerproducts.models.dao;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerCard;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CustomerCardDao extends ReactiveMongoRepository<CustomerCard, String> {

}
