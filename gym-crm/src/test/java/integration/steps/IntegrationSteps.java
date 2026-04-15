package integration.steps;

import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.dto.trainee.register.TraineeRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.trainer.register.TrainerRegisterRequest;
import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;
import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;
import integration.steps.support.IntegrationSharedState;
import integration.steps.support.TestUserSession;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

import java.time.LocalDate;

public class IntegrationSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${workload-service.base-url}")
    private String workloadServiceBaseUrl;

    @Autowired
    private IntegrationSharedState state;



    @Given("the application is running")
    public void the_application_is_running() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/actuator/health", String.class);

        assertEquals(200, response.getStatusCode().value());
    }
    @Given("a trainee is registered")
    public void a_trainee_is_registered() {
        TraineeRegisterRequest request =
                new TraineeRegisterRequest("trainee","integration", LocalDate.of(1990,1,1),"123 Integarion St");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/trainees", request, String.class);

        JsonNode data = objectMapper.readTree(response.getBody()).get("data");

        assertNotNull(data);

        String username = data.get("username").asText();
        String password = data.get("password").asText();

        TestUserSession trainee = new TestUserSession();
        trainee.setUsername(username);
        trainee.setPassword(password);

        state.setTraineeUser(trainee);

    }

    @Given("a trainer is registered")
    public void a_trainer_is_registered() {
        TrainerRegisterRequest request =
                new TrainerRegisterRequest("trainer","integration",1L);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/trainers" , request , String.class);
        JsonNode data = objectMapper.readTree(response.getBody()).get("data");

        String username  = data.get("username").asText();
        String password = data.get("password").asText();

        TestUserSession trainer = new TestUserSession();
        trainer.setUsername(username);
        trainer.setPassword(password);

        state.setTrainerUser(trainer);
    }

    @Given("the trainer is logged in")
    public void the_trainer_is_logged_in() {
        login(state.getTrainerUser());
    }

    private void login(TestUserSession user) {
        LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/api/v1/login", loginRequest, String.class);

        assertEquals(200, loginResponse.getStatusCode().value());

        JsonNode loginData = objectMapper.readTree(loginResponse.getBody()).get("data");
        assertNotNull(loginData);

        String token= loginData.get("accessToken").asText();

        user.setToken(token);
    }



    @When("a {int}-minute training is created")
    public void a_minute_training_is_created(Integer duration) {
        createTraining(duration);

    }

    private void createTraining(Integer duration){
        TestUserSession trainee = state.getTraineeUser();
        TestUserSession trainer = state.getTrainerUser();

        TrainingCreateRequest request =  new TrainingCreateRequest(
                trainee.getUsername(),
                trainer.getUsername(),
                TrainingTypeCode.CARDIO.getTrainingTypeName(),
                LocalDate.now(), duration
        );

        HttpEntity<TrainingCreateRequest> entity = new HttpEntity<>(request, trainer.authHeaders());
        ResponseEntity<String> exchange = restTemplate.exchange("/api/v1/trainings", HttpMethod.POST, entity, String.class);
        state.setResponse(exchange);
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(Integer expectedStatus) {
        assertNotNull(state.getResponse());
        assertEquals(expectedStatus.intValue(), state.getResponse().getStatusCode().value());
    }

    @Then("the trainer workload should be updated with {int} minutes")
    public void the_trainer_workload_should_be_updated_with_minutes(Integer expectedMinutes) {
        TestUserSession trainer = state.getTrainerUser();

        String url = buildTrainerWorkloadSummaryUrl(trainer.getUsername());

        HttpHeaders authHeader = trainer.authHeaders();


        await().atMost(10, SECONDS).pollInterval(2, SECONDS).untilAsserted(() -> {

            HttpEntity<Void> entity = new HttpEntity<>(authHeader);

            ResponseEntity<String> workloadResponse = new RestTemplate()
                    .exchange(url, HttpMethod.GET, entity, String.class);

            assertEquals(200, workloadResponse.getStatusCode().value());

            JsonNode data = objectMapper.readTree(workloadResponse.getBody()).get("data");
            assertNotNull(data);

            int totalDuration = data.get("yearlySummaries").get(0)
                    .get("monthlySummaries").get(0)
                    .get("totalTrainingDuration").asInt();

            assertEquals(expectedMinutes.intValue(), totalDuration);
        });
    }


    @Given("the trainee is logged in")
    public void the_trainee_is_logged_in() {
        login(state.getTraineeUser());
    }


    @Given("a {int}-minute training exists between the trainee and trainer")
    public void a_minute_training_exists_between_the_trainee_and_trainer(Integer duration) {
        createTraining(duration);
    }

    @Given("the trainer workload should be {int} minutes for the current month")
    public void the_trainer_workload_should_be_for_the_current_month(Integer minutes) {
        the_trainer_workload_should_be_updated_with_minutes(minutes);
    }

    @When("the trainee deletes their profile")
    public void the_trainee_deletes_their_profile() {

        TestUserSession trainee = state.getTraineeUser();

        HttpEntity<Void> entity = new HttpEntity<>(trainee.authHeaders());
        ResponseEntity<String> response = restTemplate.exchange("/api/v1/trainees/"+trainee.getUsername(), HttpMethod.DELETE, entity, String.class);
        state.setResponse(response);
    }


    @Then("the trainer workload summary should not be found for the current month")
    public void the_trainer_workload_summary_should_not_be_found_for_the_current_month() {

        TestUserSession trainer = state.getTrainerUser();

        String url = buildTrainerWorkloadSummaryUrl(trainer.getUsername());

        HttpHeaders authHeader = trainer.authHeaders();

        await().atMost(10, SECONDS).pollInterval(2, SECONDS).untilAsserted(() -> {
            HttpEntity<Void> entity = new HttpEntity<>(authHeader);
            try {
                new RestTemplate().exchange(url, HttpMethod.GET, entity, String.class);
                fail("Expected trainer workload summary to be removed, but it still exists.");
            } catch (org.springframework.web.client.HttpClientErrorException.NotFound ex) {
                assertEquals(404, ex.getStatusCode().value());
            }


        });


    }

    private String buildTrainerWorkloadSummaryUrl(String trainerUsername) {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        return workloadServiceBaseUrl +
                "/api/v1/workload/trainers/" + trainerUsername +
                "/summary?year=" + year + "&month=" + month;
    }
}
