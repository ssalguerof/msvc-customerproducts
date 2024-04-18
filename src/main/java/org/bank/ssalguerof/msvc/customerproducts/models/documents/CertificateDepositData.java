package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Esta clase representa la información de una cuenta a plazo fijo (certificado de depósito).
 * Contiene detalles como el monto del depósito y la fecha de vencimiento del certificado.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateDepositData {
  private Double monto;
  private Date fecVencimiento;

}
