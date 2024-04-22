package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que contiene informacion de un cuenta de ahorro y cuenta corriente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountData {
  private Double saldo;
  private Integer numMovRealizados; //numeros de movimientos realizados
}
