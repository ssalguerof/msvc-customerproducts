package org.bank.ssalguerof.msvc.customerproducts.models.services;

import org.bank.ssalguerof.msvc.customerproducts.models.dao.CustomerProductDao;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerProductServiceImpl implements CustomerProductService{
    @Autowired
    private CustomerProductDao customerProductDao;
    @Override
    public Flux<CustomerProduct> findAll() {
        return customerProductDao.findAll();
    }

    @Override
    public Mono<CustomerProduct> findbyId(String id) {
        return customerProductDao.findById(id);
    }

    @Override
    public Mono<CustomerProduct> save(CustomerProduct customerProduct) {
        return customerProductDao.save(customerProduct);
    }

    @Override
    public Mono<Void> delete(CustomerProduct customerProduct) {
        return customerProductDao.delete(customerProduct);
    }
}
