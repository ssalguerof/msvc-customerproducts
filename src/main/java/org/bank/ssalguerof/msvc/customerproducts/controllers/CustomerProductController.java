package org.bank.ssalguerof.msvc.customerproducts.controllers;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.bank.ssalguerof.msvc.customerproducts.models.services.CustomerProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/customerProduct")
public class CustomerProductController {
    @Autowired
    private CustomerProductService customerProductService;

    @GetMapping
    public Flux<CustomerProduct> findAll(){
        return customerProductService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<CustomerProduct> findById(@PathVariable String id){return customerProductService.findbyId(id);}

    @PostMapping
    public Mono<CustomerProduct> saveProduct(@RequestBody CustomerProduct customerProduct){
        return  customerProductService.save(customerProduct);
    }
    @PutMapping
    public Mono<CustomerProduct> updateProduct(@RequestBody CustomerProduct customerProduct){
        return customerProductService.findbyId(customerProduct.getId())
                .flatMap(existingProduct->{
                    BeanUtils.copyProperties(customerProduct, existingProduct, "id");
                    return  customerProductService.save(existingProduct);
                });
    }
}
