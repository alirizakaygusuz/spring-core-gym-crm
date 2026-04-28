package component.steps;

import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.update.TrainerProfileUpdateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import component.steps.support.AuthenticatedUser;
import component.steps.support.SharedState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


import static org.junit.Assert.*;

public class TrainerSteps {

    @Autowired
    private TestRestTemplate restTemplate;


    @Autowired
    private SharedState state;


    @Autowired
    private ObjectMapper objectMapper;


    private TrainerRegisterRequest registerRequest;


    private static final String ANYONE = "anyone";
    private static final String ANOTHER_TRAINER = "anotherTrainer";


    @Given("a new trainer registration request with valid data")
    public void a_new_trainer_registration_request_with_valid_data() {
        registerRequest = new TrainerRegisterRequest("Trainer", "Jane", 1L);
    }

    @When("the trainer submits the registration request")
    public void the_trainer_submits_the_registration_request() {
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/trainers", registerRequest, String.class);
        state.setResponse(response);
    }

    @Then("the response contains the trainer's username and password")
    public void the_response_contains_the_trainer_s_username_and_password() {

        assertEquals(200, state.getResponse().getStatusCode().value());
        assertNotNull(state.getResponse().getBody());
        assertTrue(state.getResponse().getBody().contains("username"));
        assertTrue(state.getResponse().getBody().contains("password"));
    }


    @Given("a new trainer registration request with missing required fields")
    public void a_new_trainer_registration_request_with_missing_required_fields() {
        registerRequest = new TrainerRegisterRequest(null, "Jane", 1L);
    }


    @Given("the trainer is logged in with valid credentials")
    public void the_trainer_is_logged_in_with_valid_credentials() throws JsonProcessingException {
        //1. Register the trainer
        TrainerRegisterRequest req = new TrainerRegisterRequest("Trainer", "Jane", 1L);
        ResponseEntity<String> registerResponse = restTemplate.postForEntity("/api/v1/trainers", req, String.class);

        //2. Extract username and password from the registration response
        JsonNode registerBody = objectMapper.readTree(registerResponse.getBody()).get("data");
        String username = registerBody.get("username").asText();
        String password = registerBody.get("password").asText();

        //3.Login to get the authentication token
        LoginRequest loginRequest = new LoginRequest(username, password);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/api/v1/login", loginRequest, String.class);


        //4. Extract token from login response
        JsonNode loginBody = objectMapper.readTree(loginResponse.getBody()).get("data");
        String accessToken = loginBody.get("accessToken").asText();

        state.initializeAuthenticatedUser(username, accessToken, req.firstName(), req.lastName());

        state.setResponse(loginResponse);

    }


    @When("the trainer requests their profile")
    public void the_trainer_requests_their_profile() {
        accessTrainerProfile(state.getUsername(), state.authHeaders());
    }

    @Then("the response contains the trainer's profile information")
    public void the_response_contains_the_trainer_s_profile_information() throws JsonProcessingException {
        assertNotNull(state.getResponse().getBody());

        JsonNode data = objectMapper.readTree(state.getResponse().getBody()).get("data");

        assertNotNull(data);
        assertEquals(state.getFirstName(), data.get("firstName").asText());
        assertEquals(state.getLastName(), data.get("lastName").asText());
    }


    @When("the trainer requests another trainer's profile")
    public void the_trainer_requests_another_trainer_s_profile() {


        accessTrainerProfile(ANOTHER_TRAINER, state.authHeaders());
    }


    @Given("the trainer is not logged in")
    public void the_trainer_is_not_logged_in() {
        state.clearAuthentication();
    }


    @When("the unauthenticated trainer attempts to access a trainer profile")
    public void the_unauthenticated_trainer_attempts_to_access_a_trainer_profile() {
        accessTrainerProfile(ANYONE, state.jsonHeaders());
    }


    private void accessTrainerProfile(String username , HttpHeaders headers) {
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = restTemplate.exchange("/api/v1/trainers/" + username, HttpMethod.GET, entity, String.class);
        state.setResponse(exchange);
    }


    @When("the trainer updates their profile with valid data")
    public void the_trainer_updates_their_profile_with_valid_data() {
        TrainerProfileUpdateRequest updateRequest
                = new TrainerProfileUpdateRequest("UpdatedFirstName", "UpdatedLastName", 2L, true);

        AuthenticatedUser user = state.getAuthenticatedUser();

        user.setFirstName(updateRequest.firstName());
        user.setLastName(updateRequest.lastName());

        updateTrainerProfile(state.getUsername(), updateRequest, state.authHeaders());
    }




    @Then("the response contains the updated trainer information")
    public void the_response_contains_the_updated_trainer_information() throws JsonProcessingException {

        assertNotNull(state.getResponse().getBody());

        JsonNode data = objectMapper.readTree(state.getResponse().getBody()).get("data");
        assertNotNull(data);

        assertEquals(state.getFirstName(), data.get("firstName").asText());
        assertEquals(state.getLastName(), data.get("lastName").asText());
    }


    @When("the trainer attempts to update another trainer's profile")
    public void the_trainer_attempts_to_update_another_trainer_s_profile() {
        TrainerProfileUpdateRequest updateRequest
                = new TrainerProfileUpdateRequest("UpdatedFirstName", "UpdatedLastName", 2L, true);

        updateTrainerProfile(ANOTHER_TRAINER, updateRequest, state.authHeaders());

    }


    @When("the unauthenticated trainer attempts to update a trainer profile")
    public void the_unauthenticated_trainer_attempts_to_update_a_trainer_profile() {
        TrainerProfileUpdateRequest updateRequest
                = new TrainerProfileUpdateRequest("UpdatedFirstName", "UpdatedLastName", 2L, true);

        updateTrainerProfile(ANYONE, updateRequest, state.jsonHeaders());
    }


    @When("the trainer attempts to update their profile with invalid data")
    public void the_trainer_attempts_to_update_their_profile_with_invalid_data() {
        TrainerProfileUpdateRequest updateRequest
                = new TrainerProfileUpdateRequest("", null, -1L, false);

        updateTrainerProfile(state.getUsername(), updateRequest, state.authHeaders());

    }

    private void updateTrainerProfile(String username, TrainerProfileUpdateRequest updateRequest, HttpHeaders headers) {
        HttpEntity<TrainerProfileUpdateRequest> entity = new HttpEntity<>(updateRequest, headers);
        ResponseEntity<String> exchange = restTemplate.exchange("/api/v1/trainers/" + username, HttpMethod.PUT, entity, String.class);
        state.setResponse(exchange);
    }


    @When("the trainer updates their status")
    public void the_trainer_updates_their_status() {
        updateTrainerStatus(state.getUsername(), false, state.authHeaders());
    }

    @When("the trainer attempts to update their status with the same value")
    public void the_trainer_attempts_to_update_their_status_with_the_same_value() {
        updateTrainerStatus(state.getUsername(), true, state.authHeaders());

    }

    @When("the trainer attempts to update another trainer's status")
    public void the_trainer_attempts_to_update_another_trainer_s_status() {
        updateTrainerStatus(ANOTHER_TRAINER, true, state.authHeaders());


    }


    @When("the unauthenticated trainer attempts to update their status")
    public void the_unauthenticated_trainer_attempts_to_update_their_status() {

        updateTrainerStatus(ANYONE, true, state.jsonHeaders());
    }

    private void updateTrainerStatus(String username, boolean isActive, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange =
                restTemplate.exchange("/api/v1/trainers/" + username + "/active-status?isActive=" + isActive, HttpMethod.PATCH, entity, String.class);
        state.setResponse(exchange);
    }


    @When("the trainer requests their trainings")
    public void the_trainer_requests_their_trainings() {
        accessTrainerTrainings(state.getUsername(), state.authHeaders());
    }


    @When("the unauthenticated trainer attempts to access their trainings")
    public void the_unauthenticated_trainer_attempts_to_access_their_trainings() {
        accessTrainerTrainings(ANYONE, state.jsonHeaders());
    }


    @When("the trainer attempts to access another trainer's trainings")
    public void the_trainer_attempts_to_access_another_trainer_s_trainings() {
        accessTrainerTrainings(ANOTHER_TRAINER, state.authHeaders());
    }

    private void accessTrainerTrainings(String username, HttpHeaders headers) {
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = restTemplate.exchange("/api/v1/trainers/" + username + "/trainings", HttpMethod.GET, entity, String.class);
        state.setResponse(exchange);
    }


}
