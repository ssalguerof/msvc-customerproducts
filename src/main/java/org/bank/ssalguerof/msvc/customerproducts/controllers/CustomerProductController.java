package org.bank.ssalguerof.msvc.customerproducts.controllers;

import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.Transaction;
import org.bank.ssalguerof.msvc.customerproducts.models.services.CustomerProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador para administrar operaciones relacionadas con la colección CustomerProduct.
 * Este controlador proporciona endpoints para administrar productos de clientes.
 */
@RestController
@RequestMapping("/api/customerProduct")
public class CustomerProductController {
  @Autowired
  private CustomerProductService customerProductService;

  @GetMapping
  public Flux<CustomerProduct> findAll() {
    return customerProductService.findAll();
  }

  @GetMapping("/{id}")
  public Mono<CustomerProduct> findById(@PathVariable String id) {
    return customerProductService.findbyId(id);
  }

  @PostMapping
  public Mono<CustomerProduct> saveProduct(@RequestBody CustomerProduct customerProduct) {
    return customerProductService.save(customerProduct);
  }

  /**
   * Metodo encargado de actualizar el producto de un cliente.
   */
  @PutMapping
  public Mono<CustomerProduct> updateProduct(@RequestBody CustomerProduct customerProduct) {
    return customerProductService.findbyId(customerProduct.getId())
      .flatMap(existingProduct -> {
        BeanUtils.copyProperties(customerProduct, existingProduct, "id");
        return customerProductService.save(existingProduct);
      });
  }

  /**
   * Metodo encargado de registrar un movimiento en un producto de un cliente.
   */
  @PutMapping("/transaction/{idCustomerProd}")
  public Mono<CustomerProduct> updateProductTransaction(
          @RequestBody Transaction transaction,
          @PathVariable String idCustomerProd) {

    return customerProductService.findbyId(idCustomerProd)
      .flatMap(existingProduct -> {
        return customerProductService.updateProductTransaction(idCustomerProd, transaction);
      });
  }

}
