package component.steps;

import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import component.steps.support.SharedState;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;


public class AuthSteps {


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SharedState state;

    @Autowired
    private ObjectMapper objectMapper;


    @Given("a seeded user exists in the system")
    public void a_seeded_user_exists_in_the_system() {
        // This step assumes that the database is pre-seeded with a user having username "john.doe" and password "password123".
    }


    @When("the user logs in with valid credentials")
    public void the_user_logs_in_with_valid_credentials() {
         login("john.doe", "password123");
    }




    @And("the response contains a JWT token")
    public void the_response_contains_a_jwt_token() {
        assertNotNull(state.getResponse());
        assertNotNull(state.getResponse().getBody());

        JsonNode data = objectMapper
                .readTree(state.getResponse().getBody())
                .get("data");

        assertNotNull(data);
        assertNotNull(data.get("accessToken"));
        assertFalse(data.get("accessToken").asText().isBlank());
    }


    @When("the user logs in with invalid password")
    public void the_user_logs_in_with_invalid_password() {
        login("john.doe", "invalidPassword");
    }


    @When("an unknown user attempts to login")
    public void an_unknown_user_attempts_to_login() {
        login("unknownUser", "somePassword");
    }


    @When("the user attempts to login {int} times with wrong password")
    public void the_user_attempts_to_login_times_with_wrong_password(Integer attempts) {
        for (int i = 0; i < attempts; i++) {
             login("john.doe", "wrongPassword");
        }
    }

    @And("the user tries to login once more")
    public void the_user_tries_to_login_once_more() {
        login("john.doe", "wrongPassword");
    }


    private void login(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest,  state.jsonHeaders());

        ResponseEntity<String> response = restTemplate.exchange("/api/v1/login", HttpMethod.POST, entity, String.class);
        state.setResponse(response);
    }

}