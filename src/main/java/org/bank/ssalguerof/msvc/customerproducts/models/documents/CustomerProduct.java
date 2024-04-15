package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "productoscliente")
public class CustomerProduct {
    @Id
    private String id;
    private String numCuenta;
    private String numTarjeta;
    private String clienteId;
    private String codTipoCliente;
    private String codProducto;
    private String nomProducto;
    private String codTipoProducto;
    private String descTipoProducto;
    private Date fecRegistroProducto;
    //Dato para Cuenta de Ahorro o Cuenta Corriente
    private AccountData datosCuentaAhorro;
    private AccountData datosCuentaCorriente;
    private CertificateDepositData datosPlazoFijo;
    private CreditData datosCreditoEmpresarial;
    private CreditData datosCreditoPersonal;
    private CreditCardData datosTarjetaCredito;
    private List<Customer> listaTitulares;

}
