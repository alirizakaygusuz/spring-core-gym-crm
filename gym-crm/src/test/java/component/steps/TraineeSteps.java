package component.steps;

import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.update.TraineeProfileUpdateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
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


import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class TraineeSteps {


    @Autowired
    private TestRestTemplate restTemplate;


    @Autowired
    private SharedState state;

    @Autowired
    private ObjectMapper objectMapper ;


    private TraineeRegisterRequest registerRequest;


    private static final String ANYONE = "anyone";
    private static final String ANOTHER_TRAINEE = "anotherTrainee";


    @Given("a new trainee registration request with valid data")
    public void a_new_trainee_registration_request_with_valid_data() {
        registerRequest = new TraineeRegisterRequest(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St, Anytown, USA"
        );


    }

    @When("the trainee submits the registration request")
    public void the_trainee_submits_the_registration_request() {
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/trainees", registerRequest, String.class);
        state.setResponse(response);
    }

    @Then("the response contains the trainee's username and password")
    public void the_response_contains_the_trainee_s_username_and_password() {

        assertEquals(200, state.getResponse().getStatusCode().value());

        assertNotNull(state.getResponse().getBody());
        assertTrue(state.getResponse().getBody().contains("username"));
        assertTrue(state.getResponse().getBody().contains("password"));
    }

    @Given("a new trainee registration request with missing required fields")
    public void a_new_trainee_registration_request_with_missing_required_fields() {
        registerRequest = new TraineeRegisterRequest(
                "John",
                null,
                LocalDate.of(1990, 1, 1),
                "123 Main St, Anytown, USA"
        );
    }


    @Given("the trainee is logged in with valid credentials")
    public void the_trainee_is_logged_in_with_valid_credentials()  {
        // 1. Register
        TraineeRegisterRequest req = new TraineeRegisterRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St"
        );
        ResponseEntity<String> registerResponse =
                restTemplate.postForEntity("/api/v1/trainees", req, String.class);

        // 2. Parse username & password
        JsonNode registerBody = objectMapper.readTree(registerResponse.getBody()).get("data");
        String username = registerBody.get("username").asText();
        String password = registerBody.get("password").asText();

        // 3. Login
        LoginRequest loginReq = new LoginRequest(username, password);
        ResponseEntity<String> loginResponse =
                restTemplate.postForEntity("/api/v1/login", loginReq, String.class);


        // 4. Parse token
        JsonNode loginBody = objectMapper.readTree(loginResponse.getBody()).get("data");

        String accessToken  = loginBody.get("accessToken").asText();

        // 5. Initialize authenticated user in shared state
        state.initializeAuthenticatedUser(username,  accessToken,"John", "Doe");
        state.setResponse(loginResponse);

    }

    @When("the trainee requests their profile")
    public void the_trainee_requests_their_profile() {
        accessTraineeProfile(state.getUsername(), state.authHeaders());
    }

    @Then("the response contains the trainee's profile information")
    public void the_response_contains_the_trainee_s_profile_information() throws JsonProcessingException {
        assertNotNull(state.getResponse().getBody());

        JsonNode data = objectMapper.readTree(state.getResponse().getBody()).get("data");

        assertNotNull(data);
        assertEquals(state.getFirstName(), data.get("firstName").asText());
        assertEquals(state.getLastName(), data.get("lastName").asText());
    }


    @When("the trainee requests another trainee's profile")
    public void the_trainee_requests_another_trainee_s_profile() throws JsonProcessingException {
        TraineeRegisterRequest traineeRegisterRequest = new TraineeRegisterRequest(
                "Ali", "Riza", LocalDate.of(1992, 2, 2), "35 Izmir "
        );

        ResponseEntity<String> registerResponse =
                restTemplate.postForEntity("/api/v1/trainees", traineeRegisterRequest, String.class);


        JsonNode data = objectMapper.readTree(registerResponse.getBody()).get("data");
        String username = data.get("username").asText();


        accessTraineeProfile(username, state.authHeaders());

    }


    @Given("the trainee is not logged in")
    public void the_trainee_is_not_logged_in() {
        state.clearAuthentication();
    }

    @When("the unauthenticated trainee attempts to access a trainee profile")
    public void the_unauthenticated_trainee_attempts_to_access_a_trainee_profile() {
        accessTraineeProfile(ANYONE, state.jsonHeaders());

    }

    private void accessTraineeProfile(String username, HttpHeaders headers) {
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> exchange = restTemplate.exchange(
                "/api/v1/trainees/" + username,
                HttpMethod.GET,
                entity,
                String.class
        );
        state.setResponse(exchange);
    }


    @When("the trainee updates their profile with valid data")
    public void the_trainee_updates_their_profile_with_valid_data() {
        TraineeProfileUpdateRequest updateRequest = new TraineeProfileUpdateRequest(
                "John",
                "Doe Updated",
                LocalDate.of(1990, 1, 1),
                "123 Main St, Anytown, USA"
                , true
        );

        updateTraineeProfile(state.getUsername(), updateRequest, state.authHeaders());

    }

    @Then("the response contains the updated trainee information")
    public void the_response_contains_the_updated_trainee_information() {

        assertNotNull(state.getResponse().getBody());
        JsonNode data = objectMapper.readTree(state.getResponse().getBody()).get("data");
        assertNotNull(data);
        assertEquals("Doe Updated", data.get("lastName").asText());
    }


    @When("the trainee attempts to update another trainee's profile")
    public void the_trainee_attempts_to_update_another_trainee_s_profile() {
        TraineeRegisterRequest traineeRegisterRequest = new TraineeRegisterRequest(
                "Ali", "Riza", LocalDate.of(1992, 2, 2), "35 Izmir "
        );

        ResponseEntity<String> registerResponse =
                restTemplate.postForEntity("/api/v1/trainees", traineeRegisterRequest, String.class);

        TraineeProfileUpdateRequest updateRequest = new TraineeProfileUpdateRequest(
                "Ali",
                "Riza Updated",
                LocalDate.of(1992, 2, 2),
                "35 Izmir "
                , true
        );

        JsonNode data = objectMapper.readTree(registerResponse.getBody()).get("data");
        String username = data.get("username").asText();


        updateTraineeProfile(username, updateRequest, state.authHeaders());
    }


    @When("the unauthenticated trainee attempts to update a trainee profile")
    public void the_unauthenticated_trainee_attempts_to_update_a_trainee_profile() {

        TraineeProfileUpdateRequest updateRequest = new TraineeProfileUpdateRequest(
                "John",
                "Doe Updated",
                LocalDate.of(1990, 1, 1),
                "123 Main St, Anytown, USA"
                , true
        );

        updateTraineeProfile(ANYONE, updateRequest, state.jsonHeaders());
    }


    @When("the trainee updates their profile with invalid data")
    public void the_trainee_updates_their_profile_with_invalid_data() {
        TraineeProfileUpdateRequest updateRequest = new TraineeProfileUpdateRequest(
                null,
                null,
                LocalDate.of(1990, 1, 1),
                "123 Main St, Anytown, USA"
                , true
        );

        updateTraineeProfile(state.getUsername(), updateRequest, state.authHeaders());

    }

    private void updateTraineeProfile(String username, TraineeProfileUpdateRequest updateRequest, HttpHeaders headers) {
        HttpEntity<TraineeProfileUpdateRequest> entity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<String> exchange = restTemplate.exchange(
                "/api/v1/trainees/" + username,
                HttpMethod.PUT,
                entity,
                String.class
        );
        state.setResponse(exchange);
    }


    @Given("a new trainee is registered and logged in with valid credentials")
    public void a_new_trainee_is_registered_and_logged_in_with_valid_credentials()  {
        registerRequest = new TraineeRegisterRequest(
                "Delete",
                "Me",
                LocalDate.of(1990, 1, 1),
                "123 Main St, Anytown, USA"
        );

        ResponseEntity<String> registerResponse =
                restTemplate.postForEntity("/api/v1/trainees", registerRequest, String.class);

        JsonNode data = objectMapper.readTree(registerResponse.getBody()).get("data");
        String username = data.get("username").asText();
        String password = data.get("password").asText();

        LoginRequest loginReq = new LoginRequest(username, password);
        ResponseEntity<String> loginResponse =
                restTemplate.postForEntity("/api/v1/login", loginReq, String.class);

        JsonNode loginBody = objectMapper.readTree(loginResponse.getBody()).get("data");

        String accessToken = loginBody.get("accessToken").asText();

        state.initializeAuthenticatedUser(
                username,
                accessToken,
                registerRequest.firstName(),
                registerRequest.lastName()

        );

        state.setResponse(loginResponse);



    }

    @When("the trainee deletes their profile")
    public void the_trainee_deletes_their_profile() {

        deleteTraineeProfile(state.getUsername(), state.authHeaders());
    }


    @When("the trainee attempts to delete another trainee's profile")
    public void the_trainee_attempts_to_delete_another_trainee_s_profile() throws JsonProcessingException {
        TraineeRegisterRequest req = new TraineeRegisterRequest(
                "Other", "Trainee", LocalDate.of(1995, 5, 5), "456 Oak Ave"
        );
        ResponseEntity<String> registerResponse =
                restTemplate.postForEntity("/api/v1/trainees", req, String.class);
        JsonNode data = objectMapper.readTree(registerResponse.getBody()).get("data");
        String otherUsername = data.get("username").asText();


        deleteTraineeProfile(otherUsername, state.authHeaders());
    }


    @When("the unauthenticated trainee attempts to delete a trainee profile")
    public void the_unauthenticated_trainee_attempts_to_delete_a_trainee_profile() {

        deleteTraineeProfile(ANYONE, state.jsonHeaders());
    }

    private void deleteTraineeProfile(String username, HttpHeaders headers) {
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = restTemplate.exchange(
                "/api/v1/trainees/" + username,
                HttpMethod.DELETE,
                entity,
                String.class
        );

        state.setResponse(exchange);
    }


    @When("the trainee updates their status")
    public void the_trainee_updates_their_status() {
        updateTraineeStatus(state.getUsername(), false, state.authHeaders());


    }


    @When("the trainee attempts to update their status with the same value")
    public void the_trainee_attempts_to_update_their_status_with_the_same_value() {
        updateTraineeStatus(state.getUsername(), true, state.authHeaders());

    }

    @When("the trainee attempts to update another trainee's status")
    public void the_trainee_attempts_to_update_another_trainee_s_status() {

        updateTraineeStatus(ANOTHER_TRAINEE, true, state.authHeaders());


    }

    @When("the unauthenticated trainee attempts to update their status")
    public void the_unauthenticated_trainee_attempts_to_update_their_status() {
        updateTraineeStatus(ANYONE, true, state.jsonHeaders());

    }

    private void updateTraineeStatus(String username, boolean isActive, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = restTemplate.exchange("/api/v1/trainees/" + username + "/active-status?isActive=" + isActive, HttpMethod.PATCH, entity, String.class);
        state.setResponse(exchange);
    }


    @When("the trainee requests the list of not assigned trainers")
    public void the_trainee_requests_the_list_of_not_assigned_trainers() {
        getNotAssignedTrainersForTrainee(state.getUsername(), state.authHeaders());
    }

    @When("the trainee requests another trainee's not assigned active trainers")
    public void the_trainee_requests_another_trainee_s_not_assigned_active_trainers() {
        getNotAssignedTrainersForTrainee(ANOTHER_TRAINEE, state.authHeaders());
    }

    @When("the unauthenticated trainee attempts to access the list of not assigned active trainers")
    public void the_unauthenticated_trainee_attempts_to_access_the_list_of_not_assigned_active_trainers() {
        getNotAssignedTrainersForTrainee(ANYONE, state.jsonHeaders());
    }


    private void getNotAssignedTrainersForTrainee(String username, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> exchange = restTemplate.exchange("/api/v1/trainees/" + username + "/trainers/not-assigned",
                HttpMethod.GET, entity, String.class);
        state.setResponse(exchange);
    }


    @When("the trainee updates their trainer list with valid trainer usernames")
    public void the_trainee_updates_their_trainer_list_with_valid_trainer_usernames() {
        updateTraineeTrainers(state.getUsername(), state.authHeaders());

    }


    @When("the trainee attempts to update another trainee's trainer list")
    public void the_trainee_attempts_to_update_another_trainee_s_trainer_list() {
        updateTraineeTrainers(ANOTHER_TRAINEE, state.authHeaders());
    }


    @When("the unauthenticated trainee attempts to update trainee's trainer list")
    public void the_unauthenticated_trainee_attempts_to_update_trainee_s_trainer_list() {

        updateTraineeTrainers(ANYONE, state.jsonHeaders());

    }

    private void updateTraineeTrainers(String username, HttpHeaders headers) {
        List<String> trainerUsernames = List.of("trainer.jane");

        HttpEntity<List<String>> entity = new HttpEntity<>(trainerUsernames, headers);

        ResponseEntity<String> exchange = restTemplate.exchange("/api/v1/trainees/" + username + "/trainers",
                HttpMethod.PUT, entity, String.class);
        state.setResponse(exchange);
    }

    @When("the trainee requests their trainings")
    public void the_trainee_requests_their_trainings() {
        accessTraineeTrainings(state.getUsername(), state.authHeaders());
    }

    @When("the trainee requests another trainee's trainings")
    public void the_trainee_requests_another_trainee_s_trainings() {

        accessTraineeTrainings(ANOTHER_TRAINEE, state.authHeaders());
    }

    @When("the unauthenticated trainee attempts to access trainee trainings")
    public void the_unauthenticated_trainee_attempts_to_access_trainee_trainings() {

        accessTraineeTrainings(ANYONE, state.jsonHeaders());
    }

    private void accessTraineeTrainings(String username, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(headers);


        ResponseEntity<String> exchange = restTemplate.exchange("/api/v1/trainees/" + username + "/trainings",
                HttpMethod.GET, entity, String.class);

        state.setResponse(exchange);
    }


}
