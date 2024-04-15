package org.bank.ssalguerof.msvc.customerproducts.models.services;

import org.bank.ssalguerof.msvc.customerproducts.models.dao.CustomerProductDao;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.*;

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
        /*Validamos para persona natural:
        * •	Un cliente personal solo puede tener un máximo de una cuenta de ahorro, una cuenta corriente o cuentas a plazo fijo.*/
        if(customerProduct.getCodTipoCliente().equals(COD_TIPOPERSONA_PERS)){
            //validamos que el producto a asignar a cliente sea los productos permitido a cliente personal
            if(!PRODUCTS_CLIENTE_PERSONAL.stream().anyMatch(codprod -> codprod.equals(customerProduct.getCodProducto()))){
                return Mono.error(new RuntimeException("El producto a registrar no esta permitido a Persona Natural"));
            }else{
                //Validaciones para el caso de un Activo
                if(customerProduct.getCodTipoProducto().equals(COD_TIPOPRODUCTO_ACTIVO)){
                    /*Un cliente personal solo puede tener un máximo de una cuenta de ahorro, una cuenta corriente o cuentas a plazo fijo.*/
                    return customerProductDao.findByClienteIdAndCodTipoClienteAndCodProductoIn(customerProduct.getClienteId(), "1",PRODUCTS_CTA_PERSONAL)
                            .hasElements()
                            .flatMap(hasElement->{
                                if (hasElement){
                                    // Ya existe un registro, devolver un Mono vacío o un error
                                    return Mono.error(new RuntimeException("El cliente ya cuenta con el Producto con el código de producto CTAAHO, CTACOR o CTAPLZ"));
                                }else{
                                    return customerProductDao.save(customerProduct);
                                }
                            });
                }
                //para el caso de un pasivo
                if(customerProduct.getCodTipoProducto().equals(COD_TIPOPRODUCTO_PASIVO)){
                    //solo se permite un solo crédito Personal.
                    if(customerProduct.getCodProducto().equals(COD_CTOPERSONAL)){
                        return customerProductDao.findByClienteIdAndCodProducto(customerProduct.getClienteId(), COD_CTOPERSONAL)
                                .hasElements()
                                .flatMap(hasElement -> {
                                    //si ya se tiene un credito personal
                                    if (hasElement){
                                        return Mono.error(new RuntimeException("Solo se permite un Crédito Personal"));
                                    }else {
                                        return customerProductDao.save(customerProduct);
                                    }

                                });
                    }
                    //para otros casos no se indica restricciones
                    return customerProductDao.save(customerProduct);
                }
            }
        }
        /*Validamos para persona Empresarial:*/
        if (customerProduct.getCodTipoCliente().equals(COD_TIPOPERSONA_EMPR)) {
            /*validamos que el producto a asignar a cliente sea los productos permitido a cliente Empresarial donde se cumpliria lo siguiente:
                •	Un cliente empresarial no puede tener una cuenta de ahorro o de plazo fijo, pero sí múltiples cuentas corrientes.(se validar)*/
            if(!PRODUCTS_CLIENTE_EMPRESARIAL.stream().anyMatch(codprod -> codprod.equals(customerProduct.getCodProducto()))){
                return Mono.error(new RuntimeException("El producto a registrar no esta permitido a Persona Empresarial"));
            }else{
                /*Validaciones para el caso de un Activo y Pasivos
                * - No existe restricciones
                * */

                    return customerProductDao.save(customerProduct);
            }

        }

        return Mono.empty();
    }

    @Override
    public Mono<Void> delete(CustomerProduct customerProduct) {
        return customerProductDao.delete(customerProduct);
    }


}
