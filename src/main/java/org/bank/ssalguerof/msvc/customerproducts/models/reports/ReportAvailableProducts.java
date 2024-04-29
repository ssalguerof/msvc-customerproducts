package org.bank.ssalguerof.msvc.customerproducts.models.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Clase contiene informacion de un reporte de productos disponibles para un cliente.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportAvailableProducts {
  private String clienteId;
  private List<AvailableProduct> availableProductList;

}
