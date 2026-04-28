package com.alirizakaygusuz.gymcrm.workload_service.component;

import com.alirizakaygusuz.gymcrm.workload_service.WorkloadServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

@CucumberContextConfiguration
@SpringBootTest(
        classes = WorkloadServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
@ComponentScan("com.alirizakaygusuz.gymcrm.workload_service.component")
public class CucumberSpringConfiguration {

    @LocalServerPort
    protected int port;

    static GenericContainer<?> mongo =
            new GenericContainer<>("mongo:7.0")
                    .withExposedPorts(27017)
                    .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin")
                    .withEnv("MONGO_INITDB_ROOT_PASSWORD", "admin");

    static GenericContainer<?> artemis =
            new GenericContainer<>("apache/activemq-artemis:2.31.2")
                    .withExposedPorts(61616)
                    .withEnv("ARTEMIS_USER", "admin")
                    .withEnv("ARTEMIS_PASSWORD", "admin");

    static {
        mongo.start();
        artemis.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.host", mongo::getHost);
        registry.add("spring.mongodb.port", () -> mongo.getMappedPort(27017));
        registry.add("spring.mongodb.database", () -> "test");
        registry.add("spring.mongodb.username", () -> "admin");
        registry.add("spring.mongodb.password", () -> "admin");
        registry.add("spring.mongodb.authentication-database", () -> "admin");
        registry.add("spring.mongodb.auto-index-creation", () -> true);

        registry.add("spring.artemis.broker-url",
                () -> "tcp://" + artemis.getHost() + ":" + artemis.getMappedPort(61616));
        registry.add("spring.artemis.user", () -> "admin");
        registry.add("spring.artemis.password", () -> "admin");

        registry.add("security.jwt.secret",
                () -> "test-secret-key-that-is-at-least-32-characters-long-for-hmac");
        registry.add("server.port", () -> "0");
    }
}