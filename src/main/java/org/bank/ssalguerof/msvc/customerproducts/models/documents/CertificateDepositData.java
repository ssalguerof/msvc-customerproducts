package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateDepositData {
    private Double monto;
    private Date fecVencimiento;

}
