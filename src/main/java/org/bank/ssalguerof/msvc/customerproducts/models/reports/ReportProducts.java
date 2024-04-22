package org.bank.ssalguerof.msvc.customerproducts.models.reports;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa un reporte de productos con sus promedios mensuales.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportProducts {
  private String clienteId;
  private List<ProductAverage> listProductsAverage;
}
