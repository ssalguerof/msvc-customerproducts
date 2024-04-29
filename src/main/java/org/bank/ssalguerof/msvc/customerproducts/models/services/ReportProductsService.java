package org.bank.ssalguerof.msvc.customerproducts.models.services;

import java.time.LocalDate;
import java.util.Date;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportProductCommission;
import org.bank.ssalguerof.msvc.customerproducts.models.reports.ReportProducts;
import reactor.core.publisher.Mono;

/**
 * Interfaz que define servicios para la generación de informes relacionados con productos.
 */
public interface ReportProductsService {
  public Mono<ReportProducts> generateReportAverage(String clienteId,
                                                    Date fechaReporte);

  public Mono<ReportProductCommission> generateReportCommission(String clienteId,
                                                                LocalDate fechaReporte);

  public Mono<ReportProducts> generateReportProductsCustomer(String clienteId, String codTipoCliente);
}
