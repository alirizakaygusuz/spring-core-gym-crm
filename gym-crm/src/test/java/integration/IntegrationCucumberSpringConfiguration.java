package integration;

import com.alirizakaygusuz.gymcrm.Application;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@AutoConfigureTestRestTemplate
@ComponentScan(basePackages = {
        "com.alirizakaygusuz.gymcrm",
        "integration"
})
public class IntegrationCucumberSpringConfiguration {

}