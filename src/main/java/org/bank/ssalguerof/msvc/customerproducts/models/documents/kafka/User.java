package org.bank.ssalguerof.msvc.customerproducts.models.documents.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;                  // Identificador único del cliente
    private String codTipoDocumento;
    private String nombres;
    private String ApePaterno;
    private String Apematerno;
    private String numCelular;
    private String numImei;
    private String correo;
    private Double saldo;
    private String indTarjetaAsociada;
    private String numTarjeta;
    private List<Transaccion> transaccionList;
}
