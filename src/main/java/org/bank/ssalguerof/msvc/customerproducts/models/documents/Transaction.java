package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    private String id;
    private String idProductoCliente;
    private String idCliente;
    private String codTipoMovimiento;
    private String descTipoMovimiento;
    private Double monto;
    private Date fecMovimiento;
    private String descripcion;
}
