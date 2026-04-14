package component;

import com.alirizakaygusuz.gymcrm.Application;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@CucumberContextConfiguration
@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
@ComponentScan("component.steps")
public class CucumberSpringConfiguration {

    @LocalServerPort
    protected int port;

    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7-alpine")
                    .withExposedPorts(6379);

    static GenericContainer<?> artemis =
            new GenericContainer<>("apache/activemq-artemis:2.31.2")
                    .withExposedPorts(61616)
                    .withEnv("ARTEMIS_USER", "admin")
                    .withEnv("ARTEMIS_PASSWORD", "admin");

    static {
        postgres.start();
        redis.start();
        artemis.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver");

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port",
                () -> redis.getMappedPort(6379));

        registry.add("spring.artemis.broker-url",
                () -> "tcp://localhost:" + artemis.getMappedPort(61616));
        registry.add("spring.artemis.user", () -> "admin");
        registry.add("spring.artemis.password", () -> "admin");
    }
}