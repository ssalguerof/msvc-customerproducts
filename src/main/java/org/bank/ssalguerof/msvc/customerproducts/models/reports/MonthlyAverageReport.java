package org.bank.ssalguerof.msvc.customerproducts.models.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que contiene la información de resumen de Saldos.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MonthlyAverageReport {
  private int dayOfMonth;         // Día del mes
  private double saldoPromedio;   // Saldo promedio diario

}
