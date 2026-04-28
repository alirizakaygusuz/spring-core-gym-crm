package integration.support;

import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Getter
@Setter
public class IntegrationSharedState {

    private ResponseEntity<String> response;


    private TestUserSession traineeUser;
    private TestUserSession trainerUser;




}
