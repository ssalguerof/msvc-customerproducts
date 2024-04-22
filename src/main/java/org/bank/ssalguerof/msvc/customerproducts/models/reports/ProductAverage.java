package org.bank.ssalguerof.msvc.customerproducts.models.reports;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa el promedio de un producto con informe mensual de saldos promedio.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductAverage {
  private String codProducto;
  private String nomProducto;
  private String codTipoProducto;
  private String descTipoProducto;
  private String periodo;
  private List<MonthlyAverageReport> monthlyAverageReportList;
}
