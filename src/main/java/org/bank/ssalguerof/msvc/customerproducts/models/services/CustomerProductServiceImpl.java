package org.bank.ssalguerof.msvc.customerproducts.models.services;



import java.util.*;

import org.bank.ssalguerof.msvc.customerproducts.models.dao.CustomerProductDao;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.Transaction;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.AvailableProduct;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportAvailableProducts;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportProducts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.*;
import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.DESC_TARJCREDITO;

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
  public Flux<CustomerProduct> findbyClienteId(String clienteId) {
    return customerProductDao.findByClienteId(clienteId);
  }

  @Override
  public Mono<CustomerProduct> save(CustomerProduct customerProduct) {

    return validarDeudaVencida(customerProduct)
      .flatMap(tieneDeudaVencida -> {
        if (tieneDeudaVencida) {
          return Mono.error(new RuntimeException("El cliente tiene una deuda vencida y no se tienen productos disponibles"));
        } else {
          // Validar el tipo de cliente
          if (customerProduct.getCodTipoCliente().equals(COD_TIPOPERSONA_PERS)) {
            // Validar si el producto está permitido para clientes personales
            if (!PRODUCTS_CLIENTE_PERSONAL.contains(customerProduct.getCodProducto())) {
              return Mono.error(new RuntimeException("El producto no está permitido para Persona Natural"));
            }

            // Validar el tipo de producto (Activo o Pasivo)
            if (customerProduct.getCodTipoProducto().equals(COD_TIPOPRODUCTO_ACTIVO)) {
              // Validar que solo tenga un máximo de una cuenta de ahorro, corriente o plazo fijo
              return validatePersonalActiveProduct(customerProduct);
            } else if (customerProduct.getCodTipoProducto().equals(COD_TIPOPRODUCTO_PASIVO)) {
              // Validar que solo tenga un crédito personal
              return validatePersonalPassiveProduct(customerProduct);
            }
          } else if (customerProduct.getCodTipoCliente().equals(COD_TIPOPERSONA_EMPR)) {
            // Validar si el producto está permitido para clientes empresariales
            if (!PRODUCTS_CLIENTE_EMPRESARIAL.contains(customerProduct.getCodProducto())) {
              return Mono.error(new RuntimeException("El producto no está permitido para Persona Empresarial"));
            }

            // Validar el tipo de producto (Activo o Pasivo)
            if (customerProduct.getCodProducto().equals(COD_CTAMYPE)) {
              // Validar que tenga una cuenta corriente y una tarjeta de crédito
              return validateBusinessMypeProduct(customerProduct);
            }
          }
        }
        return Mono.empty();
      });

  }

  private Mono<CustomerProduct> validatePersonalActiveProduct(CustomerProduct customerProduct) {
    // Validar que no tenga más de un producto activo permitido
    return customerProductDao.findByClienteIdAndCodTipoClienteAndCodProductoIn(
        customerProduct.getClienteId(), "1", PRODUCTS_CTA_PERSONAL)
      .hasElements()
      .flatMap(hasElement -> {
        if (hasElement) {
          return Mono.error(new RuntimeException("El cliente ya tiene un Producto Activo registrado"));
        } else {
          // Para CTAAHORROVIP, validar que tenga una tarjeta de crédito
          if (customerProduct.getCodProducto().equals(COD_CTAAHORROVIP)) {
            return validateCreditCardExistence(customerProduct);
          } else {
            return customerProductDao.save(customerProduct);
          }
        }
      });
  }

  private Mono<CustomerProduct> validateCreditCardExistence(CustomerProduct customerProduct) {
    // Validar que el cliente tenga una tarjeta de crédito
    return customerProductDao.findByClienteIdAndCodProducto(customerProduct.getClienteId(), COD_TARJCREDITO)
      .hasElements()
      .flatMap(hasElement -> {
        if (hasElement) {
          return customerProductDao.save(customerProduct);
        } else {
          return Mono.error(new RuntimeException("El cliente no tiene una tarjeta de crédito"));
        }
      });
  }

  private Mono<CustomerProduct> validatePersonalPassiveProduct(CustomerProduct customerProduct) {
    // Validar que solo tenga un crédito personal
    if (customerProduct.getCodProducto().equals(COD_CTOPERSONAL)) {
      return customerProductDao.findByClienteIdAndCodProducto(customerProduct.getClienteId(), COD_CTOPERSONAL)
        .hasElements()
        .flatMap(hasElement -> {
          if (hasElement) {
            return Mono.error(new RuntimeException("Solo se permite un Crédito Personal"));
          } else {
            return customerProductDao.save(customerProduct);
          }
        });
    }

    return customerProductDao.save(customerProduct); // Para otros productos, no hay restricciones
  }

  private Mono<CustomerProduct> validateBusinessMypeProduct(CustomerProduct customerProduct) {
    // Validar que tenga una cuenta corriente y una tarjeta de crédito
    return customerProductDao.findByClienteIdAndCodTipoClienteAndCodProductoIn(
        customerProduct.getClienteId(), customerProduct.getCodTipoCliente(),
        Arrays.asList(COD_CTACORRIENTE, COD_TARJCREDITO))
      .collectList()
      .flatMap(customerProducts -> {
        boolean tieneCuentaCorriente = customerProducts.stream()
            .anyMatch(product -> product.getCodProducto().equals(COD_CTACORRIENTE));
        boolean tieneTarjetaCredito = customerProducts.stream()
            .anyMatch(product -> product.getCodProducto().equals(COD_TARJCREDITO));

        if (tieneCuentaCorriente && tieneTarjetaCredito) {
          return customerProductDao.save(customerProduct);
        } else {
          return Mono.error(new RuntimeException("El cliente no cumple con los requisitos para tener una cuenta MYPE"));
        }
      });
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

  private Boolean validarComisionTransaccion(CustomerProduct customerProduct,
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

  @Override
  public Mono<ReportAvailableProducts> generateReportProductsCustomer(String clienteId, String codTipoCliente) {
    CustomerProduct customerProduct = new CustomerProduct();
    customerProduct.setClienteId(clienteId);
    customerProduct.setCodTipoCliente(codTipoCliente);

    return validarDeudaVencida(customerProduct)
      .flatMap(tieneDeudaVencida -> {
        if (tieneDeudaVencida) {
          return Mono.error(new RuntimeException("El cliente tiene una deuda vencida y no se tienen productos disponibles"));
        } else {
          return generateReportCustomerProductos(customerProduct);
        }
      });

  }

  private Mono<Boolean> validarDeudaVencida(CustomerProduct customerProduct){
    Mono<Boolean> tieneDeudaMono = Mono.empty();

    if(customerProduct.getCodTipoCliente().equals(COD_TIPOPERSONA_PERS)){
      //Consultamos si existe un pasivo para el cliente
      Mono<List<CustomerProduct>> customerOverdueDebtProductsMono = customerProductDao.findByClienteIdAndCodTipoClienteAndCodProductoIn(
          customerProduct.getClienteId(), customerProduct.getCodTipoCliente(), PASSIVE_PRODUCTS_PERSONAL)
          .collectList();

      tieneDeudaMono = customerOverdueDebtProductsMono
        .switchIfEmpty(Mono.just(Collections.emptyList())) // Si el Mono está vacío, proporciona una lista vacía
        .map(products -> {
          return products.stream()
            .anyMatch(customerProduct1 -> {
              Boolean indDeudaVencida = false;
              switch (customerProduct1.getCodProducto()) {
                case COD_CTOPERSONAL:
                  indDeudaVencida = IND_TIENE_DEUDA.equals(customerProduct1.getDatosCreditoPersonal().getIndVencido());
                  break;
                case COD_TARJCREDITO:
                  indDeudaVencida = IND_TIENE_DEUDA.equals(customerProduct1.getDatosTarjetaCredito().getIndVencido());
                  break;
              }
              return indDeudaVencida;
            });
        });


    }else if(customerProduct.getCodTipoCliente().equals(COD_TIPOPERSONA_EMPR)){
      //Consultamos si existe un pasivo para el cliente
      Mono<List<CustomerProduct>> customerOverdueDebtProductsMono = customerProductDao.findByClienteIdAndCodTipoClienteAndCodProductoIn(
          customerProduct.getClienteId(), customerProduct.getCodTipoCliente(), PASSIVE_PRODUCTS_EMPRESARIAL)
        .collectList();

      tieneDeudaMono = customerOverdueDebtProductsMono
        .switchIfEmpty(Mono.just(Collections.emptyList())) // Si el Mono está vacío, proporciona una lista vacía
        .map(products -> {
          return products.stream()
            .anyMatch(customerProduct1 -> {
              Boolean indDeudaVencida = false;
              switch (customerProduct1.getCodProducto()) {
                case COD_CTOEMPRESARIAL:
                  indDeudaVencida = IND_TIENE_DEUDA.equals(customerProduct1.getDatosCreditoEmpresarial().getIndVencido());
                  break;
                case COD_TARJCREDITO:
                  indDeudaVencida = IND_TIENE_DEUDA.equals(customerProduct1.getDatosTarjetaCredito().getIndVencido());
                  break;
              }
              return indDeudaVencida;
            });
        });

    }

    return tieneDeudaMono;

  }

  public Mono<ReportAvailableProducts> generateReportCustomerProductos(CustomerProduct customerProduct){

    if(COD_TIPOPERSONA_PERS.equals(customerProduct.getCodTipoCliente())){
      List<AvailableProduct> availableProductList = new ArrayList<>();
      //validamos si puede tener una cuenta de Ahorro, Cuenta Corriente o Cuenta Plazo
      return customerProductDao.findByClienteIdAndCodTipoClienteAndCodProductoIn(
          customerProduct.getClienteId(), customerProduct.getCodTipoCliente(), PRODUCTS_CTA_PERSONAL)
        .hasElements()
        .flatMap(hasElement -> {
          if (hasElement) {
            availableProductList.add(new AvailableProduct(COD_CTAAHORRO, DESC_CTAAHORRO, "0"));
            availableProductList.add(new AvailableProduct(COD_CTACORRIENTE, DESC_CTACORRIENTE, "0"));
            availableProductList.add(new AvailableProduct(COD_CTAPLAZOFIJO, DESC_CTAPLAZOFIJO, "0"));
            availableProductList.add(new AvailableProduct(COD_CTAAHORROVIP, DESC_CTAAHORROVIP, "0"));
          } else {
            availableProductList.add(new AvailableProduct(COD_CTAAHORRO, DESC_CTAAHORRO, "1"));
            availableProductList.add(new AvailableProduct(COD_CTACORRIENTE, DESC_CTACORRIENTE, "1"));
            availableProductList.add(new AvailableProduct(COD_CTAPLAZOFIJO, DESC_CTAPLAZOFIJO, "1"));

            //Validamos si se puede tener una cuenta VIP
            return customerProductDao.findByClienteIdAndCodProducto(customerProduct.getClienteId(), COD_TARJCREDITO)
              .hasElements()
              .flatMap(hasElementVip -> {
                if (hasElementVip) {
                  availableProductList.add(new AvailableProduct(COD_CTAAHORROVIP, DESC_CTAAHORROVIP, "1"));
                } else {
                  availableProductList.add(new AvailableProduct(COD_CTAAHORROVIP, DESC_CTAAHORROVIP, "0"));
                }

                //Validamos si puede un crédito personal
                return customerProductDao.findByClienteIdAndCodProducto(customerProduct.getClienteId(), COD_CTOPERSONAL)
                  .hasElements()
                  .flatMap(hasPersonalCredit -> {
                    if (hasPersonalCredit) {
                      availableProductList.add(new AvailableProduct(COD_CTOPERSONAL, DESC_CTOPERSONAL, "0"));
                    } else {
                      availableProductList.add(new AvailableProduct(COD_CTOPERSONAL, DESC_CTOPERSONAL, "1"));
                    }

                    // Validamos el producto tarjeta de crédito
                    availableProductList.add(new AvailableProduct(COD_TARJCREDITO, DESC_TARJCREDITO, "1"));

                    // Creamos y retornamos el objeto ReportAvailableProducts
                    ReportAvailableProducts reportAvailableProducts = new ReportAvailableProducts();
                    reportAvailableProducts.setClienteId(customerProduct.getClienteId());
                    reportAvailableProducts.setAvailableProductList(availableProductList);

                    return Mono.just(reportAvailableProducts);
                  });
              });
          }

          // Creamos y retornamos el objeto ReportAvailableProducts
          ReportAvailableProducts reportAvailableProducts = new ReportAvailableProducts();
          reportAvailableProducts.setClienteId(customerProduct.getClienteId());
          reportAvailableProducts.setAvailableProductList(availableProductList);

          return Mono.just(reportAvailableProducts);
        });

    } else if (COD_TIPOPERSONA_EMPR.equals(customerProduct.getCodTipoCliente())) {
      List<AvailableProduct> availableProductList = new ArrayList<>();
      //se puede tener varias cuentas corrientes
      availableProductList.add(new AvailableProduct(COD_CTACORRIENTE, DESC_CTACORRIENTE, "1"));
      //validamos si credito empresarial
      availableProductList.add(new AvailableProduct(COD_CTOEMPRESARIAL, DESC_CTOEMPRESARIAL, "1"));
      //validamos tarjeta de crédito
      availableProductList.add(new AvailableProduct(COD_TARJCREDITO, DESC_TARJCREDITO, "1"));

      //validamos cuenta MYPE
      return customerProductDao.findByClienteIdAndCodTipoClienteAndCodProductoIn(
        customerProduct.getClienteId(), customerProduct.getCodTipoCliente(),
        Arrays.asList(COD_CTACORRIENTE, COD_TARJCREDITO))
        .collectList()
        .flatMap(customerProducts -> {
          // Validar que se tenga ambos productos
          // Verificar si los productos contienen COD_CTACORRIENTE y COD_TARJCREDITO
          boolean containsCTACorriente = customerProducts.stream()
            .anyMatch(product -> COD_CTACORRIENTE.equals(product.getCodProducto()));
          boolean containsTarjetaCredito = customerProducts.stream()
            .anyMatch(product -> COD_TARJCREDITO.equals(product.getCodProducto()));

          if (containsCTACorriente && containsTarjetaCredito) {
            availableProductList.add(new AvailableProduct(COD_CTAMYPE, DESC_CTAMYPE, "1"));
          } else {
            availableProductList.add(new AvailableProduct(COD_CTAMYPE, DESC_CTAMYPE, "0"));
          }
          ReportAvailableProducts reportAvailableProducts = new ReportAvailableProducts();
          reportAvailableProducts.setClienteId(customerProduct.getClienteId());
          reportAvailableProducts.setAvailableProductList(availableProductList);
          return Mono.just(reportAvailableProducts);
        });
    }
    return Mono.empty();
  }

}
