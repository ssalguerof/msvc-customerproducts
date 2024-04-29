package org.bank.ssalguerof.msvc.customerproducts.models.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.CustomerProduct;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.Transaction;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.Commission;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.MonthlyAverageReport;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ProductAverage;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ProductCommission;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportProductCommission;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportProducts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.bank.ssalguerof.msvc.customerproducts.utils.Constantes.*;

/**
 * Implementación del servicio para generar informes de productos.
 */
@Service
public class ReportProductsServiceImpl implements ReportProductsService {
  @Autowired
  private CustomerProductService customerProductService;

  @Override
  public Mono<ReportProducts> generateReportAverage(String clienteId, Date fechaReporte) {
    Flux<CustomerProduct> listCustomerProduct = customerProductService.findbyClienteId(clienteId);

    return listCustomerProduct
      .flatMap(customerProduct -> {
        Flux<Transaction> transactionsOfMonth = filterTransactionsOfMonth(
            customerProduct, fechaReporte);
        return calculateDailyAverageBalances(transactionsOfMonth)
          .collectList()
          .flatMap(dailyAverages -> createReportProducts(customerProduct, dailyAverages));
      })
      .singleOrEmpty(); // Convertir Flux a Mono
  }

  private Flux<Transaction> filterTransactionsOfMonth(
      CustomerProduct customerProduct, Date fechaReporte) {
    LocalDate reportDate = fechaReporte.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate firstDayOfMonth = reportDate.withDayOfMonth(1);
    LocalDate firstDayOfNextMonth = firstDayOfMonth.plusMonths(1);

    return Flux.fromIterable(customerProduct.getListaTransactions())
      .filter(transaction -> {
        LocalDate transactionDate = transaction.getFecRegistro().toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDate();
        return !transactionDate.isBefore(firstDayOfMonth)
          && transactionDate.isBefore(firstDayOfNextMonth);
      });
  }

  private Flux<Double> calculateDailyAverageBalances(Flux<Transaction> transactionsOfMonth) {
    return transactionsOfMonth
      .collectMultimap(Transaction::getFecRegistro)
      .flatMapIterable(Map::entrySet)
      .map(entry -> {
        LocalDate transactionDate = entry.getKey()
            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<Transaction> transactions = (List<Transaction>) entry.getValue();
        double totalBalance = 0.0;

        for (Transaction transaction : transactions) {
          if (COD_MOV_DEPOCTA.equals(transaction.getCodTipoMovimiento())) {
            totalBalance += transaction.getMonto();
          } else if (COD_MOV_RETICTA.equals(transaction.getCodTipoMovimiento())) {
            totalBalance -= transaction.getMonto();
          }
        }

        double dailyAverageBalance = totalBalance / transactions.size();
        return Pair.of(transactionDate.getDayOfMonth(), dailyAverageBalance);
      })
      .collectMap(Pair::getKey, Pair::getValue)
      .flatMapIterable(map -> {
        int daysInMonth = map.keySet().stream().max(Integer::compareTo).orElse(0);
        List<Double> dailyAverages = new ArrayList<>(daysInMonth);

        for (int dayOfMonth = 1; dayOfMonth <= daysInMonth; dayOfMonth++) {
          dailyAverages.add(map.getOrDefault(dayOfMonth, 0.0));
        }

        return dailyAverages;
      });
  }

  private Mono<ReportProducts> createReportProducts(
      CustomerProduct customerProduct, List<Double> dailyAverages) {
    return Mono.fromCallable(() -> {
      ReportProducts reportProducts = new ReportProducts();
      reportProducts.setClienteId(customerProduct.getClienteId());

      final List<ProductAverage> productAverages = new ArrayList<>();
      ProductAverage productAverage = new ProductAverage();
      productAverage.setCodProducto(customerProduct.getCodProducto());
      productAverage.setNomProducto(customerProduct.getNomProducto());
      productAverage.setCodTipoProducto(customerProduct.getCodTipoProducto());
      productAverage.setDescTipoProducto(customerProduct.getDescTipoProducto());
      // Obtener la fecha actual
      LocalDate currentDate = LocalDate.now();
      // Puedes ajustar el periodo según tus necesidades
      productAverage.setPeriodo(getYearMonthString(currentDate));

      List<MonthlyAverageReport> monthlyReports = new ArrayList<>();
      int daysInMonth = LocalDate.now().lengthOfMonth();

      // Iterar sobre los saldos promedio diarios
      for (int dayOfMonth = 1; dayOfMonth <= daysInMonth; dayOfMonth++) {
        MonthlyAverageReport monthlyReport = new MonthlyAverageReport();
        monthlyReport.setDayOfMonth(dayOfMonth);

        if (dayOfMonth <= dailyAverages.size()) {
          monthlyReport.setSaldoPromedio(dailyAverages.get(dayOfMonth - 1));
        } else {
          monthlyReport.setSaldoPromedio(0.0); // Si no hay datos disponibles, saldo promedio es 0
        }

        monthlyReports.add(monthlyReport);
      }

      productAverage.setMonthlyAverageReportList(monthlyReports);
      productAverages.add(productAverage);

      reportProducts.setListProductsAverage(productAverages);

      return reportProducts;
    });
  }

  // Método para obtener el año y mes en formato "yyyy-MM" de una LocalDate
  private static String getYearMonthString(LocalDate date) {
    // Formateador para el año y mes (yyyy-MM)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

    // Obtener el año y mes como una cadena formateada
    String formattedYearMonth = date.format(formatter);

    return formattedYearMonth;
  }

  //Generar Reporte
  @Override
  public Mono<ReportProductCommission> generateReportCommission(
         String clienteId, LocalDate fechaReporte) {
    Flux<CustomerProduct> listCustomerProduct = customerProductService.findbyClienteId(clienteId);

    return listCustomerProduct
      .collectList()
      .map(customerProducts -> createReportCommission(customerProducts, fechaReporte));
  }

  private ReportProductCommission createReportCommission(
      List<CustomerProduct> customerProducts, LocalDate fechaReporte) {
    ReportProductCommission report = new ReportProductCommission();
    report.setPeriodo(getYearMonthString(fechaReporte));
    // Tomar el clienteId del primer producto
    report.setClienteId(customerProducts.get(0).getClienteId());

    List<ProductCommission> productCommissionList = calculateCommissions(customerProducts);
    report.setProductCommissionList(productCommissionList);

    return report;
  }



  private List<ProductCommission> calculateCommissions(List<CustomerProduct> customerProducts) {

    List<ProductCommission> commissions = new ArrayList<>();

    for (CustomerProduct product : customerProducts) {
      List<Commission> commissionList = product.getListaTransactions().stream()
          .filter(transaction -> transaction.getCodTransferencia().equals(COD_TRANS_COBRO_COMISION))
          .map(transaction -> {
            Commission commission = new Commission(transaction.getCodTipoMovimiento(),
                transaction.getDescTipoMovimiento(), transaction.getMonto(),
                transaction.getCodTransferencia(), transaction.getDesTransferencia());
            return commission;
          })
          .collect(Collectors.toList());
      commissions.add(new ProductCommission(product.getCodProducto(),
          product.getNomProducto(), product.getCodTipoProducto(),
          product.getDescTipoProducto(), commissionList));
    }

    return commissions;
  }

  @Override
  public Mono<ReportProducts> generateReportProductsCustomer(String clienteId, String codTipoCliente) {
    if(COD_TIPOPERSONA_PERS.equals(codTipoCliente)){
      //validamos si puede tener una cuenta de Ahorro, Cuenta Corriente o Cuenta Plazo


    } else if (COD_TIPOPERSONA_EMPR.equals(codTipoCliente)) {

    }
    return null;
  }

}
