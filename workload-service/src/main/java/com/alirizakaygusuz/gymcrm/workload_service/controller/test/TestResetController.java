package com.alirizakaygusuz.gymcrm.workload_service.controller.test;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("integration")
@RestController
@RequiredArgsConstructor
public class TestResetController {

    private final MongoTemplate mongoTemplate;

    @DeleteMapping("/api/v1/test/reset")
    public ResponseEntity<Void> reset() {
        mongoTemplate.dropCollection("trainer_workload_summary");
        return ResponseEntity.ok().build();
    }
}
