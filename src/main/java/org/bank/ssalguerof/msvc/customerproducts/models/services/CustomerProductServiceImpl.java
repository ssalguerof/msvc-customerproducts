package org.bank.ssalguerof.msvc.customerproducts.models.services;



import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.CANT_MAX_TRANS;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_CTAAHORRO;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_CTAAHORROVIP;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_CTACORRIENTE;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_CTAMYPE;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_CTAPLAZOFIJO;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_CTOEMPRESARIAL;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_CTOPERSONAL;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_MOV_CARGACONSUMO;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_MOV_DEPOCTA;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_MOV_PAGOCREDITO;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_MOV_RETICTA;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_TARJCREDITO;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_TIPOPERSONA_EMPR;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_TIPOPERSONA_PERS;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_TIPOPRODUCTO_ACTIVO;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_TIPOPRODUCTO_PASIVO;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.COD_TRANS_COBRO_COMISION;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.DESC_MOV_CARGACONSUMO;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.DESC_MOV_DEPOCTA;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.DESC_MOV_PAGOCREDITO;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.DESC_MOV_RETICTA;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.DES_TRANS_BANCARIA_CUENTAS;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.DES_TRANS_BANCARIA_TERCEROS;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.DES_TRANS_COBRO_COMISION;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.MONTO_COMISION;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.PRODUCTS_CLIENTE_EMPRESARIAL;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.PRODUCTS_CLIENTE_PERSONAL;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.PRODUCTS_CTA_PERSONAL;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.TRANSACTIONS_CTAS;

import java.util.Arrays;
import java.util.Date;
import org.bank.ssalguerof.msvc.customerproducts.models.dao.CustomerProductDao;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación del servicio CustomerProductService que administra la información de
 * CustomerProduct.
 * */
@Service
public class CustomerProductServiceImpl implements CustomerProductService {
  @Autowired
  private CustomerProductDao customerProductDao;
  private static final Logger log = LoggerFactory.getLogger(CustomerProductServiceImpl.class);

  @Override
  public Flux<CustomerProduct> findAll() {
    return customerProductDao.findAll();
  }

  @Override
  public Mono<CustomerProduct> findbyId(String id) {
    return customerProductDao.findById(id);
  }

  @Override
  public Mono<CustomerProduct> findbyNumCuenta(String numCuenta) {
    return customerProductDao.findByNumCuenta(numCuenta);
  }

  @Override
  public Mono<CustomerProduct> save(CustomerProduct customerProduct) {
    //Validamos para persona natural:
    //Un cliente personal solo puede tener un máximo de una cuenta de ahorro, una cuenta
    //corriente o cuentas a plazo fijo.
    if (customerProduct.getCodTipoCliente().equals(COD_TIPOPERSONA_PERS)) {
      //validamos que el producto a asignar a cliente sea los productos permitido a cliente personal
      if (!PRODUCTS_CLIENTE_PERSONAL.stream().anyMatch(codprod -> codprod.equals(
          customerProduct.getCodProducto()))) {
        return Mono.error(new RuntimeException("El producto a registrar no esta permitido a "
          + "Persona Natural"));
      } else {
        //Validaciones para el caso de un Activo
        if (customerProduct.getCodTipoProducto().equals(COD_TIPOPRODUCTO_ACTIVO)) {

          //Un cliente personal solo puede tener un máximo de una cuenta de ahorro,
          //una cuenta corriente o cuentas a plazo fijo.

          return customerProductDao.findByClienteIdAndCodTipoClienteAndCodProductoIn(
            customerProduct.getClienteId(), "1", PRODUCTS_CTA_PERSONAL)
            .hasElements()
            .flatMap(hasElement -> {
              if (hasElement) {
                // Ya existe un registro, devolver un Mono vacío o un error
                return Mono.error(new RuntimeException("El cliente ya cuenta con el Producto con "
                  + "el código de producto CTAAHO, CTACOR, CTAPLZ, CTAVIP"));
              } else {

                if (customerProduct.getCodProducto().equals(COD_CTAAHORROVIP)) {
                  //validamos que el cliente tenga una tarjeta de crédito
                  return customerProductDao.findByClienteIdAndCodProducto(
                    customerProduct.getClienteId(),
                      COD_TARJCREDITO).hasElements()
                    .flatMap(hasElementCred -> {
                      if (hasElementCred) {
                        return customerProductDao.save(customerProduct);
                      } else {
                        // Si no cuenta con una tarjeta de crédito, devolver un Mono
                        // vacío o un error
                        return Mono.error(new RuntimeException("El cliente no cuenta "
                                                          + "con una tarjeta de crédito"));
                      }
                    });
                } else {
                  return customerProductDao.save(customerProduct);
                }

              }
            });
        }
        //para el caso de un pasivo
        if (customerProduct.getCodTipoProducto().equals(COD_TIPOPRODUCTO_PASIVO)) {
          //solo se permite un solo crédito Personal.
          if (customerProduct.getCodProducto().equals(COD_CTOPERSONAL)) {
            return customerProductDao.findByClienteIdAndCodProducto(customerProduct.getClienteId(),
                COD_CTOPERSONAL)
              .hasElements()
              .flatMap(hasElement -> {
                //si ya se tiene un credito personal
                if (hasElement) {
                  return Mono.error(new RuntimeException("Solo se permite un Crédito Personal"));
                } else {
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
      //validamos que el producto a asignar a cliente sea los productos permitido a cliente
      //Empresarial donde se cumpliria lo siguiente:
      //•Un cliente empresarial no puede tener una cuenta de ahorro o de plazo fijo,
      //pero sí múltiples cuentas corrientes.(se validar)
      if (!PRODUCTS_CLIENTE_EMPRESARIAL.stream().anyMatch(codprod ->
           codprod.equals(customerProduct.getCodProducto()))) {
        return Mono.error(new RuntimeException("El producto a registrar no esta permitido "
          + "a Persona Empresarial"));
      } else {
        /*Validaciones para el caso de un Activo y Pasivos
         * - No existe restricciones
         * */

        if (customerProduct.getCodProducto().equals(COD_CTAMYPE)) {
          return customerProductDao.findByClienteIdAndCodTipoClienteAndCodProductoIn(
            customerProduct.getClienteId(), customerProduct.getCodTipoCliente(),
            Arrays.asList(COD_CTACORRIENTE, COD_TARJCREDITO))
            .collectList()
            .flatMap(customerProducts -> {
              // Verificar si la lista de productos contiene ambos productos requeridos
              boolean tieneCuentaCorriente = customerProducts.stream()
                    .anyMatch(product -> product.getCodProducto().equals(COD_CTACORRIENTE));
              boolean tieneTarjetaCredito = customerProducts.stream()
                    .anyMatch(product -> product.getCodProducto().equals(COD_TARJCREDITO));
                      if (tieneCuentaCorriente && tieneTarjetaCredito) {
                        // El cliente tiene ambos productos requeridos
                        return customerProductDao.save(customerProduct);
                      } else {
                        // El cliente no tiene algún producto requerido
                        return Mono.error(new RuntimeException("El cliente no cumple con los "
                          + "requisitos para tener una cuenta MYPE"));
                      }
            }
            );

        } else {
          //para los otros productos existe requisitos
          return customerProductDao.save(customerProduct);
        }
      }

    }
    return Mono.empty();
  }

  @Override
  public Mono<Void> delete(CustomerProduct customerProduct) {
    return customerProductDao.delete(customerProduct);
  }

  /*
   * funcion que permite agregar una transaccion en un producto
   * */
  @Override
  public Mono<CustomerProduct> updateProductTransaction(
                                  String idCustomerProd, Transaction transaction) {

    return customerProductDao.findById(idCustomerProd)
      .flatMap(customerProduct -> {
        customerProduct.getListaTransactions().add(transaction);

        /*
         * Actualizamos los saldos del producto según el movimiento realizado
         * */

        if (transaction.getCodTipoMovimiento().equals(COD_MOV_DEPOCTA)
            && customerProduct.getCodProducto().equals(COD_CTAAHORRO)) {
          customerProduct.getDatosCuentaAhorro().setSaldo(customerProduct.getDatosCuentaAhorro()
              .getSaldo() + transaction.getMonto());
          //validamos si la transaccion corresponde a una transacción en la cuenta
          if (transaction.getIndOrigenTransaccion().equals("1")) {
            customerProduct.getDatosCuentaAhorro().setNumMovRealizados(customerProduct
                .getDatosCuentaAhorro().getNumMovRealizados() + 1);
          }
        }
        if (transaction.getCodTipoMovimiento().equals(COD_MOV_DEPOCTA)
            && customerProduct.getCodProducto().equals(COD_CTACORRIENTE)) {
          customerProduct.getDatosCuentaCorriente().setSaldo(customerProduct
              .getDatosCuentaCorriente().getSaldo() + transaction.getMonto());
          //validamos si la transaccion corresponde a una transacción en la cuenta
          if (transaction.getIndOrigenTransaccion().equals("1")) {
            customerProduct.getDatosCuentaCorriente().setNumMovRealizados(customerProduct
                .getDatosCuentaCorriente().getNumMovRealizados() + 1);
          }
        }
        if (transaction.getCodTipoMovimiento().equals(COD_MOV_DEPOCTA)
            && customerProduct.getCodProducto().equals(COD_CTAPLAZOFIJO)) {

          customerProduct.getDatosPlazoFijo().setMonto(customerProduct.getDatosPlazoFijo()
              .getMonto() + transaction.getMonto());
        }

        if (transaction.getCodTipoMovimiento().equals(COD_MOV_RETICTA)
            && customerProduct.getCodProducto().equals(COD_CTAAHORRO)) {
          /*Validamos que se tenga saldo en la cuenta para poder realizar un retiro*/
          if (transaction.getMonto() > customerProduct.getDatosCuentaAhorro().getSaldo()) {
            return Mono.error(new RuntimeException("El Monto de la transferencia es mayor al "
              + "saldo de la cuenta"));
          } else {
            customerProduct.getDatosCuentaAhorro().setSaldo(customerProduct.getDatosCuentaAhorro()
                .getSaldo() - transaction.getMonto());
            //validamos si la transaccion corresponde a una transacción en la cuenta
            if (transaction.getIndOrigenTransaccion().equals("1")) {
              customerProduct.getDatosCuentaAhorro().setNumMovRealizados(customerProduct
                  .getDatosCuentaAhorro().getNumMovRealizados() + 1);
            }
          }
        }
        if (transaction.getCodTipoMovimiento().equals(COD_MOV_RETICTA)
            && customerProduct.getCodProducto().equals(COD_CTACORRIENTE)) {
          /*Validamos que se tenga saldo en la cuenta para poder realizar un retiro*/
          if (transaction.getMonto() > customerProduct.getDatosCuentaCorriente().getSaldo()) {
            return Mono.error(new RuntimeException("El Monto de la transferencia es mayor al saldo "
                + "de la cuenta"));
          } else {
            customerProduct.getDatosCuentaCorriente().setSaldo(customerProduct
                .getDatosCuentaCorriente().getSaldo() - transaction.getMonto());
            //validamos si la transaccion corresponde a una transacción en la cuenta
            if (transaction.getIndOrigenTransaccion().equals("1")) {
              customerProduct.getDatosCuentaCorriente().setNumMovRealizados(customerProduct
                  .getDatosCuentaCorriente().getNumMovRealizados() + 1);
            }
          }
        }
        if (transaction.getCodTipoMovimiento().equals(COD_MOV_RETICTA)
            && customerProduct.getCodProducto().equals(COD_CTAPLAZOFIJO)) {
          /*Validamos que se tenga saldo en la cuenta para poder realizar un retiro*/
          if (transaction.getMonto() > customerProduct.getDatosPlazoFijo().getMonto()) {
            return Mono.error(new RuntimeException("El Monto de la transferencia es mayor al "
              + "saldo de la cuenta"));
          } else {
            customerProduct.getDatosPlazoFijo().setMonto(customerProduct.getDatosPlazoFijo()
                .getMonto() - transaction.getMonto());
          }
        }
        if (transaction.getCodTipoMovimiento().equals(COD_MOV_PAGOCREDITO)
            && customerProduct.getCodProducto().equals(COD_CTOPERSONAL)) {
          customerProduct.getDatosCreditoPersonal().setCuotasPagadas(customerProduct
              .getDatosCreditoPersonal().getCuotasPagadas() + 1);
          customerProduct.getDatosCreditoPersonal().setSaldoPendiente(customerProduct
              .getDatosCreditoPersonal().getSaldoPendiente() - transaction.getMonto());
        }
        if (transaction.getCodTipoMovimiento().equals(COD_MOV_PAGOCREDITO)
            && customerProduct.getCodProducto().equals(COD_CTOEMPRESARIAL)) {
          customerProduct.getDatosCreditoEmpresarial().setCuotasPagadas(customerProduct
              .getDatosCreditoEmpresarial().getCuotasPagadas() + 1);
          customerProduct.getDatosCreditoEmpresarial().setSaldoPendiente(customerProduct
              .getDatosCreditoEmpresarial().getSaldoPendiente() - transaction.getMonto());
        }
        if (transaction.getCodTipoMovimiento().equals(COD_MOV_PAGOCREDITO)
            && customerProduct.getCodProducto().equals(COD_TARJCREDITO)) {
          customerProduct.getDatosTarjetaCredito().setSaldoUtilizado(customerProduct
              .getDatosTarjetaCredito().getSaldoUtilizado() - transaction.getMonto());
        }


        if (transaction.getCodTipoMovimiento().equals(COD_MOV_CARGACONSUMO)
            && customerProduct.getCodProducto().equals(COD_TARJCREDITO)) {
          /*Validamos que se tenga saldo en la linea de credito de la tarjeta*/
          Double montoDisponible = customerProduct.getDatosTarjetaCredito().getLimiteCredito()
              - customerProduct.getDatosTarjetaCredito().getSaldoUtilizado();
          if (transaction.getMonto() > montoDisponible) {
            return Mono.error(new RuntimeException("El Monto de la transferencia es mayor al saldo "
                                                      + "de la linea de credito disponible"));
          } else {
            customerProduct.getDatosTarjetaCredito().setSaldoUtilizado(customerProduct
                .getDatosTarjetaCredito().getSaldoUtilizado() + transaction.getMonto());
          }
        }
        return customerProductDao.save(customerProduct);
      })
      .doOnSuccess(productoActualizado -> {
        // Maneja la respuesta del producto actualizado
        log.info("Se agregó un nuevo movimiento al producto: " + productoActualizado.getId());
      });
  }

  @Override
  public Mono<CustomerProduct> transferProductTransaction(String numCtaOrigen, String numCtaDestino,
                                                          Transaction transaction) {

    // Buscamos el producto cliente de origen
    Mono<CustomerProduct> origenProductMono = customerProductDao.findByNumCuenta(numCtaOrigen);

    // Buscamos el producto cliente de destino
    Mono<CustomerProduct> destinoProductMono = customerProductDao.findByNumCuenta(numCtaDestino);

    return origenProductMono.zipWith(destinoProductMono)
      .flatMap(tuple -> {
        CustomerProduct origenProduct = tuple.getT1();
        CustomerProduct destinoProduct = tuple.getT2();

        // Validamos que ambos productos existan
        if (origenProduct != null && destinoProduct != null) {

          // Validamos que la transacción sea de tipo permitido para transferencia
          if (TRANSACTIONS_CTAS.contains(transaction.getCodTransferencia())) {

            // Validamos la comisión de la transacción
            if (validarComisionTransaccion(origenProduct, transaction)) {

              // Registramos un depósito en la cuenta Destino
              transaction.setCodTipoMovimiento(COD_MOV_DEPOCTA);
              transaction.setDescTipoMovimiento(DESC_MOV_DEPOCTA);
              transaction.setIndOrigenTransaccion("0");

              return updateProductTransaction(destinoProduct.getId(), transaction)
                .then(Mono.defer(() -> {

                  // Validamos el límite de transacciones
                  if (obtenerNumeroMovimientos(origenProduct) >= CANT_MAX_TRANS) {
                    // Realizamos la transacción de cobro de comisión
                    Transaction commissionTransaction = new Transaction(COD_MOV_RETICTA,
                        DESC_MOV_RETICTA, MONTO_COMISION, new Date(), COD_TRANS_COBRO_COMISION,
                        DES_TRANS_COBRO_COMISION, "0");
                    return updateProductTransaction(origenProduct.getId(), commissionTransaction)
                      .then(Mono.defer(() -> {
                        //registramos un retiro en la cuenta Origen
                        transaction.setCodTipoMovimiento(COD_MOV_RETICTA);
                        transaction.setDescTipoMovimiento(DESC_MOV_RETICTA);
                        transaction.setIndOrigenTransaccion("1");
                        return updateProductTransaction(origenProduct.getId(),
                          transaction);
                      }));
                  } else {
                    // Registramos un retiro en la cuenta Origen sin cobro de comisión
                    transaction.setCodTipoMovimiento(COD_MOV_RETICTA);
                    transaction.setDescTipoMovimiento(DESC_MOV_RETICTA);
                    transaction.setIndOrigenTransaccion("1");
                    return updateProductTransaction(origenProduct.getId(), transaction);
                  }
                }));
            } else {
              return Mono.error(new RuntimeException("El monto de la transferencia "
                + "es mayor al saldo de la cuenta"));
            }
          } else {
            return Mono.error(new RuntimeException("Tipo de transacción no permitida "
              + "para transferencia"));
          }
        } else {
          // En caso de que uno o ambos productos no existan, retornar un error
          return Mono.error(new RuntimeException("No se encontraron los productos de origen "
            + "y/o destino"));
        }
      });
  }

  /**
   * Obtiene el saldo del producto del cliente en función del tipo de producto.
   *
   *  @param customerProduct El objeto CustomerProduct del cual se desea obtener el saldo.
   *  @return El saldo del producto.
   *  @throws IllegalArgumentException Si el tipo de producto no es válido.
   */
  @Override
  public Double getSaldoProductoCliente(CustomerProduct customerProduct) {
    switch (customerProduct.getCodTipoProducto()) {
      case COD_CTAAHORRO:
        return customerProduct.getDatosCuentaAhorro().getSaldo();
      case COD_CTACORRIENTE:
        return customerProduct.getDatosCuentaCorriente().getSaldo();
      case COD_CTAPLAZOFIJO:
        return customerProduct.getDatosPlazoFijo().getMonto();
      case COD_CTOEMPRESARIAL:
        return customerProduct.getDatosCreditoEmpresarial().getSaldoPendiente();
      case COD_CTOPERSONAL:
        return customerProduct.getDatosCreditoPersonal().getSaldoPendiente();
      case COD_TARJCREDITO:
        return customerProduct.getDatosTarjetaCredito().getSaldoUtilizado();
      default:
        throw new IllegalArgumentException("Tipo de producto no válido");
    }
  }

  @Override
  public Boolean validarComisionTransaccion(CustomerProduct customerProduct,
                                            Transaction transaction) {
    Double montoTransferencia = calcularMontoTransferencia(customerProduct, transaction);

    if (montoTransferencia == null) {
      return null;
    }

    return montoTransferencia <= obtenerSaldoDisponible(customerProduct);
  }


  private Boolean validarCantMaxMov(CustomerProduct customerProduct, Transaction transaction) {
    return obtenerNumeroMovimientos(customerProduct) <= CANT_MAX_TRANS;
  }

  private Double calcularMontoTransferencia(CustomerProduct customerProduct,
                                            Transaction transaction) {
    Double monto = transaction.getMonto();
    Integer numMovimientos = obtenerNumeroMovimientos(customerProduct);

    if (numMovimientos != null) {
      if (numMovimientos >= CANT_MAX_TRANS) {

        monto += MONTO_COMISION;
      }
    } else {
      return null; // Indicar un error en la validación
    }

    return monto;
  }

  private Integer obtenerNumeroMovimientos(CustomerProduct customerProduct) {
    if (COD_CTAAHORRO.equals(customerProduct.getCodProducto())) {
      return customerProduct.getDatosCuentaAhorro().getNumMovRealizados();
    } else if (COD_CTACORRIENTE.equals(customerProduct.getCodProducto())) {
      return customerProduct.getDatosCuentaCorriente().getNumMovRealizados();
    }

    return 0; // Indicar un error en la validación
  }

  private Double obtenerSaldoDisponible(CustomerProduct customerProduct) {
    if (COD_CTAAHORRO.equals(customerProduct.getCodProducto())) {
      return customerProduct.getDatosCuentaAhorro().getSaldo();
    } else if (COD_CTACORRIENTE.equals(customerProduct.getCodProducto())) {
      return customerProduct.getDatosCuentaCorriente().getSaldo();
    }
    return null; // Indicar un error en la validación
  }


}
