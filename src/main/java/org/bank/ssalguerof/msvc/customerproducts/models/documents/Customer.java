package org.bank.ssalguerof.msvc.customerproducts.models.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "clientes")
public class Customer {
    @Id
    private String id;
    private String codTipoCliente;
    private String DescTipoCliente;
    private String nombre;
    private String apePaterno;
    private String apeMaterno;
    private String codTipoDocumento;
    private String descTipoDocumento;
    private String numDocumento;
    private String email;
    private String telefono;
    private Date   fecRegistro;

}
