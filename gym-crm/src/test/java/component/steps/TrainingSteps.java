package component.steps;

import com.alirizakaygusuz.gymcrm.dto.training.TrainingCreateRequest;
import com.alirizakaygusuz.gymcrm.model.TrainingTypeCode;
import com.fasterxml.jackson.core.JsonProcessingException;

import component.steps.support.SharedState;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class TrainingSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SharedState state;

    @Autowired
    private ObjectMapper objectMapper ;

    @When("the trainer creates a new training session with valid data")
    public void the_trainer_creates_a_new_training_session_with_valid_data() {
        TrainingCreateRequest  request =
                new TrainingCreateRequest("john.doe",state.getUsername(), TrainingTypeCode.YOGA.getTrainingTypeName() ,
                        LocalDate.of(2026,04,03),60);

        createTraining(request, state.authHeaders(state.getToken()));

    }


    @When("the trainer creates a new training session with missing required fields")
    public void the_trainer_creates_a_new_training_session_with_missing_required_fields() {
        TrainingCreateRequest  request =
                new TrainingCreateRequest(null,state.getUsername(), TrainingTypeCode.YOGA.getTrainingTypeName() ,
                        LocalDate.of(2026,04,03),60);

        createTraining(request, state.authHeaders(state.getToken()));


    }

    @When("the unauthenticated trainer attempts to create a new training session")
    public void the_unauthenticated_trainer_attempts_to_create_a_new_training_session() {
        TrainingCreateRequest  request =
                new TrainingCreateRequest("anyone", "anyone", TrainingTypeCode.YOGA.getTrainingTypeName() ,
                        LocalDate.of(2026,04,03),60);

        createTraining(request, state.jsonHeaders());


    }

    @When("the trainer creates a training session for another trainer")
    public void the_trainer_creates_a_training_session_for_another_trainer() {
        TrainingCreateRequest  request =
                new TrainingCreateRequest("john.doe","anotherTrainer", TrainingTypeCode.YOGA.getTrainingTypeName() ,
                        LocalDate.of(2026,04,03),60);

        createTraining(request, state.authHeaders(state.getToken()));



    }

    private void createTraining(TrainingCreateRequest request, HttpHeaders headers) {
        HttpEntity<TrainingCreateRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> exchange = restTemplate.exchange("/api/v1/trainings", HttpMethod.POST, entity, String.class);
        state.setResponse(exchange);
    }

    @When("requests the list of training types")
    public void requests_the_list_of_training_types() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/trainings/types", String.class);
        state.setResponse(response);

    }

    @Then("the response contains a list of training types")
    public void the_response_contains_a_list_of_training_types() throws JsonProcessingException {
        assertNotNull(state.getResponse().getBody());

        JsonNode data = objectMapper.readTree(state.getResponse().getBody()).get("data");

        assertNotNull(data);
        assertTrue(data.isArray());
        assertTrue(data.size() > 0);


    }

}
