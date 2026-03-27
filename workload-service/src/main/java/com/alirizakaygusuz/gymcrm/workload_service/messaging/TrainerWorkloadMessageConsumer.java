package com.alirizakaygusuz.gymcrm.workload_service.messaging;


import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadRequest;
import com.alirizakaygusuz.gymcrm.workload_service.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadMessageConsumer {

    private final TrainerWorkloadService trainerWorkloadService;

    private static final  String TRANSACTION_ID = "transactionId";


    @JmsListener(destination = "${messaging.queues.workload}")
    public void handleTrainerWorkloadEvent(TrainerWorkloadRequest request , @Header(name = TRANSACTION_ID, required = false) String transactionId) {

        if (transactionId != null) {
            MDC.put(TRANSACTION_ID, transactionId);
        }
        log.info("Received Trainer Workload Event with Transaction ID: {} for trainer={} | action={}", transactionId, request.username(), request.actionType());

        try{

            trainerWorkloadService.processTrainerWorkload(request);

            log.info("Processed Trainer Workload Event for trainer={} | action={}", request.username(), request.actionType());
        } finally {
            MDC.clear();
        }

    }
}
