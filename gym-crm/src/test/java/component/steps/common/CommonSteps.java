package component.steps.common;

import component.steps.support.SharedState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

public class CommonSteps {

    @Autowired
    private SharedState state;

    @Autowired
    private TestRestTemplate restTemplate;


    @Given("the application is running")
    public void the_application_is_running() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/actuator/health", String.class);

        assertEquals(200, response.getStatusCode().value());
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(int expectedStatus) {
        assertNotNull(state.getResponse());
        assertEquals(expectedStatus, state.getResponse().getStatusCode().value());
    }
}
