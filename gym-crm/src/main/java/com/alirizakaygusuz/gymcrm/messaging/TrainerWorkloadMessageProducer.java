package com.alirizakaygusuz.gymcrm.messaging;


import com.alirizakaygusuz.gymcrm.messaging.dto.TrainerWorkloadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadMessageProducer {


    private final JmsTemplate jmsTemplate;

    private static final  String TRANSACTION_ID = "transactionId";


    @Value("${messaging.queues.workload}")
    private String destination;


    public void sendTrainerWorkloadEvent(TrainerWorkloadRequest request) {
        log.info("Sending Trainer Workload Event  queue={} |  trainer={}  | action={}", destination, request.username()
                , request.actionType());

        jmsTemplate.convertAndSend(destination , request , message -> {
            String transactionId = MDC.get(TRANSACTION_ID);

            if (transactionId != null) {
                message.setStringProperty(TRANSACTION_ID, transactionId);
            }

            return message;
        });


        log.info("Trainer Workload Event sent successfully for trainer={} | action={}", request.username(), request.actionType());
    }

}
