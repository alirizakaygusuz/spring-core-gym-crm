package component.steps.common;

import component.steps.support.SharedState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class CommonSteps {

    @Autowired
    private SharedState state;

    @Given("the application is running")
    public void the_application_is_running() {
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(int expectedStatus) {
        assertNotNull(state.getResponse());
        assertEquals(expectedStatus, state.getResponse().getStatusCode().value());
    }
}
