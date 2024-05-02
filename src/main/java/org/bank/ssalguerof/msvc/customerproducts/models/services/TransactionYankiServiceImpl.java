package org.bank.ssalguerof.msvc.customerproducts.models.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.TransactionCard;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.kafka.Transaccion;
import org.bank.ssalguerof.msvc.customerproducts.models.documents.kafka.User;
import org.bank.ssalguerof.msvc.customerproducts.utils.Constantes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;

@Service
public class TransactionYankiServiceImpl implements TransactionYankiService{
    @Autowired
    CustomerCardService customerCardService;

    @Autowired


    private static final Logger log = LoggerFactory.getLogger(TransactionYankiServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    @KafkaListener(topics = "yanki-topic", groupId = "banking-group")
    public Mono<Boolean> processMessage(String event) throws JsonProcessingException {
        log.info("Mensaje recibido {} ", event);
        try {
            User user = objectMapper.readValue(event, User.class);
            log.info("Objeto User creado: {}", user.toString());
            Transaccion transaccion = user.getTransaccionList().get(0);

            return customerCardService.findByNumTarjeta(user.getNumTarjeta())
                    .flatMap(customerCard -> {
                        TransactionCard transaction = null;
                        switch (transaccion.getCodTipoRecarga()) {
                            case "01":
                                transaction = new TransactionCard(null, new Date(), "Enviar pago Yape",
                                        Constantes.COD_TRANS_TARJ_ENVIAR_YAPE, Constantes.COD_ESTADO_TRANS_PENDIENTE
                                        , transaccion.getMonto());
                                break;
                            case "02":
                                transaction = new TransactionCard(null, new Date(), "Enviar pago Yape",
                                        Constantes.COD_TRANS_TARJ_RECIBIR_YAPE, Constantes.COD_ESTADO_TRANS_PENDIENTE
                                        , transaccion.getMonto());
                                break;
                        }

                        if (customerCard != null) {
                            customerCardService.addTransactionCard(customerCard.getId(), transaction);
                            return Mono.just(true);
                        } else {
                            return Mono.error(new RuntimeException("Tipo de transacción no válido"));
                        }
                    })
                    .onErrorResume(error -> {
                        log.error("Error al procesar el mensaje: {}", error.getMessage());
                        return Mono.just(false); // Devuelve falso en caso de error
                    });
        } catch (JsonProcessingException e) {
            log.error("Error al procesar el mensaje JSON: {}", e.getMessage());
            return Mono.just(false); // Devuelve falso en caso de error
        }
    }
}
