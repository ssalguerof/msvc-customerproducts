package org.bank.ssalguerof.msvc.customerproducts.models.services;

import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.*;

import org.bank.ssalguerof.msvc.customerproducts.models.dao.CustomerCardDao;
import org.bank.ssalguerof.msvc.customerproducts.models.dao.CustomerProductDao;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerCard;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.Transaction;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.TransactionCard;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportCardBalance;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportCardMovements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerCardServiceImpl implements CustomerCardService {
  @Autowired
  private CustomerCardDao customerCardDao;

  @Autowired
  private CustomerProductDao customerProductDao;

  @Autowired
  private CustomerProductService customerProductService;

  private static final Logger log = LoggerFactory.getLogger(CustomerCardServiceImpl.class);

  @Override
  public Flux<CustomerCard> findAll() {
    return customerCardDao.findAll();
  }

  @Override
  public Mono<CustomerCard> save(CustomerCard customerCard) {

    return customerProductDao.findByClienteIdAndNumCuenta(customerCard.getClienteId(),
           customerCard.getNumCuentaPrincipal())
      .hasElement()
      .flatMap(hasElement -> {
        if (hasElement) {
          Optional cuentasOptional = Optional.ofNullable(customerCard.getCuentasSecundarias());

          if(cuentasOptional.isPresent()){
            return customerProductDao.findByClienteIdAndNumCuentaIn(customerCard.getClienteId(),
              customerCard.getCuentasSecundarias())
              .collectList()
              .flatMap(customerProducts -> {
                List<String> customerAccountsList = customerProducts.stream().map(CustomerProduct::getNumCuenta).collect(Collectors.toList());
                Boolean cuentasValidas = customerAccountsList.containsAll(customerAccountsList);
                if(cuentasValidas){
                  return customerCardDao.save(customerCard);
                }
                return Mono.error(new RuntimeException("La lista de cuentas secundarias es incorrecta"));
              });
          }else{
            return customerCardDao.save(customerCard);
          }

        }
          return Mono.error(new RuntimeException("La cuenta principal indicada es incorrecta"));

      });

  }

  @Override
  public Mono<CustomerCard> addTransactionCard(String idCard, TransactionCard transactionCard) {

    return customerCardDao.findById(idCard)
      .flatMap(customerCard -> {
        if (customerCard == null) {
          return Mono.error(new RuntimeException("El idCard no es válido"));
        }

        return processAccount(customerCard.getNumCuentaPrincipal(), transactionCard, customerCard);
      });
  }

  private Mono<CustomerCard> processAccount(String numCuenta, TransactionCard transactionCard, CustomerCard customerCard) {
    return customerProductDao.findByNumCuenta(numCuenta)
      .flatMap(customerProduct -> {
        if (customerProduct == null) {
          return Mono.error(new RuntimeException("Número de cuenta principal inválido"));
        }

        switch (transactionCard.getCodTipoTransaccion()) {
          case COD_TRANS_TARJ_RETIRO:
            // Lógica para procesar un retiro de efectivo
            Transaction transaction_ret = new Transaction(COD_MOV_RETICTA, DESC_MOV_RETICTA, transactionCard.getMonto(),
              transactionCard.getFecha(), COD_TRANS_TARJETA_RETIRO, DES_TRANS_TARJETA_RETIRO, IND_ORIGEN_TRANS_SI);

            return customerProductService.updateProductTransaction(customerProduct.getId(), transaction_ret)
              .flatMap(customerProduct1 -> {
                if(!Optional.ofNullable(customerCard.getTransactionCardList()).isPresent()){
                  customerCard.setTransactionCardList(new ArrayList<>());
                }
                customerCard.getTransactionCardList().add(transactionCard);
                return customerCardDao.save(customerCard);
              })
              .onErrorResume(error -> {
                log.info(error.getMessage());
                return processNextAccount(customerCard, transactionCard);
              });
          case COD_TRANS_TARJ_PAGO:
            // Lógica para procesar un pago
            Transaction transaction_pago = new Transaction(COD_MOV_RETICTA, DESC_MOV_RETICTA, transactionCard.getMonto(),
              transactionCard.getFecha(), COD_TRANS_TARJETA_PAGO, DES_TRANS_TARJETA_PAGO, IND_ORIGEN_TRANS_SI);

            return customerProductService.updateProductTransaction(customerProduct.getId(), transaction_pago)
              .flatMap(customerProduct1 -> {
                customerCard.getTransactionCardList().add(transactionCard);
                return customerCardDao.save(customerCard);
              })
              .onErrorResume(error -> processNextAccount(customerCard, transactionCard));
          default:
            return Mono.error(new RuntimeException("Tipo de transacción no válido"));
        }
      });
  }

  private Mono<CustomerCard> processNextAccount(CustomerCard customerCard, TransactionCard transactionCard) {
    List<String> cuentasSecundarias = customerCard.getCuentasSecundarias();
    if (cuentasSecundarias.isEmpty()) {
      return Mono.error(new RuntimeException("No se pudieron procesar las cuentas secundarias"));
    }

    String nextAccount = cuentasSecundarias.remove(0);
    return processAccount(nextAccount, transactionCard, customerCard)
      .switchIfEmpty(processNextAccount(customerCard, transactionCard));
  }


  @Override
  public Mono<ReportCardMovements> generateReportCardMovements(String idCard) {
    return customerCardDao.findById(idCard)
      .flatMap(customerCard -> {
        if(Optional.ofNullable(customerCard).isPresent()){
          return Mono.just(new ReportCardMovements(
            customerCard.getId(), customerCard.getClienteId(), customerCard.getCodTipoCliente(),
            customerCard.getNumTarjeta(), customerCard.getCodTipoTarjeta(), customerCard.getDescTipoTarjeta(),
            customerCard.getNumCuentaPrincipal(), customerCard.getCuentasSecundarias(),
            customerCard.getTransactionCardList().stream()
              .sorted(Comparator.comparing(TransactionCard::getFecha))
              .limit(10).collect(Collectors.toList())
          ));
        }else {
          return Mono.error(new RuntimeException("el idCard es incorrecto"));
        }
      });
  }

  @Override
  public Mono<ReportCardBalance> generateReportCardBalance(String idCard) {
    return customerCardDao.findById(idCard)
      .flatMap(customerCard -> {
        if (customerCard == null) {
          return Mono.error(new RuntimeException("El idCard no es válido"));
        }

        return customerProductDao.findByNumCuenta(customerCard.getNumCuentaPrincipal())
          .flatMap(customerProduct -> {
            if(Optional.ofNullable(customerProduct).isPresent()){
              switch (customerProduct.getCodProducto()) {
                case COD_CTAAHORRO:
                  return Mono.just(new ReportCardBalance(customerCard.getId(), customerCard.getClienteId()
                    , customerCard.getNumTarjeta(), customerCard.getCodTipoTarjeta(), customerCard.getDescTipoTarjeta()
                    , customerCard.getNumCuentaPrincipal(), customerProduct.getCodProducto(), customerProduct.getNomProducto()
                    , customerProduct.getDatosCuentaAhorro().getSaldo()));

                case COD_CTACORRIENTE:
                  return Mono.just(new ReportCardBalance(customerCard.getId(), customerCard.getClienteId()
                    , customerCard.getNumTarjeta(), customerCard.getCodTipoTarjeta(), customerCard.getDescTipoTarjeta()
                    , customerCard.getNumCuentaPrincipal(), customerProduct.getCodProducto(), customerProduct.getNomProducto()
                    , customerProduct.getDatosCuentaCorriente().getSaldo()));
              }
            }
              return Mono.error(new RuntimeException("Numero de cuenta principal incorrecto"));
          });
      });
  }


}
