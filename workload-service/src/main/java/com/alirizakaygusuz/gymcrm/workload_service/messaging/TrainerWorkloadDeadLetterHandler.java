package com.alirizakaygusuz.gymcrm.workload_service.messaging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TrainerWorkloadDeadLetterHandler {

    private static final String TRANSACTION_ID = "transactionId";

    @JmsListener(destination = "DLQ")
    public void handleDeadLetter(
            Message<?> message,
            @Header(name = TRANSACTION_ID, required = false) String transactionId) {

        if (transactionId != null) {
            MDC.put(TRANSACTION_ID, transactionId);
        }

        try {
            log.error("Dead letter received | transactionId={} | payload={}",
                    transactionId, message.getPayload());
        } finally {
            MDC.clear();
        }
    }
}
