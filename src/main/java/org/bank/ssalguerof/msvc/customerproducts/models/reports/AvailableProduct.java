package org.bank.ssalguerof.msvc.customerproducts.models.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que contiene informacion del producto disponible para el cliente.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AvailableProduct {
  private String codProducto;
  private String descProducto;
  private String indDisponible;
}
