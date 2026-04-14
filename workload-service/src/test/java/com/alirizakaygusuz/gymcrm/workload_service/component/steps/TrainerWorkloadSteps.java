package com.alirizakaygusuz.gymcrm.workload_service.component.steps;


import com.alirizakaygusuz.gymcrm.workload_service.component.support.JwtFactoryTest;
import com.alirizakaygusuz.gymcrm.workload_service.dto.workload.TrainerWorkloadRequest;
import com.alirizakaygusuz.gymcrm.workload_service.enums.ActionType;
import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadMonthlySummary;
import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadSummary;
import com.alirizakaygusuz.gymcrm.workload_service.model.TrainerWorkloadYearlySummary;
import com.alirizakaygusuz.gymcrm.workload_service.repository.TrainerWorkloadSummaryRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;


public class TrainerWorkloadSteps {


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SharedState state;

    @Autowired
    private JwtFactoryTest jwtFactoryTest;

    @Autowired
    private TrainerWorkloadSummaryRepository summaryRepository;


    @Before
    public void setup() {
        summaryRepository.deleteAll();
    }

    @Given("the application is running")
    public void the_application_is_running() {
        // This step can be used to ensure that the application context is loaded and the server is running.
    }

    @Given("the user is authenticated")
    public void the_user_is_authenticated() {
        String accessToken = jwtFactoryTest.generateToken("trainer.jane", List.of("ROLE_TRAINER"));
        state.setToken(accessToken);
    }

    @Given("a trainer workload entry exists")
    public void a_trainer_workload_entry_exists() {
        // monthly
        TrainerWorkloadMonthlySummary monthly = new TrainerWorkloadMonthlySummary();
        monthly.setMonth(6);
        monthly.setTotalTrainingDuration(60);

        // yearly
        TrainerWorkloadYearlySummary yearly = new TrainerWorkloadYearlySummary();
        yearly.setYear(2024);
        yearly.setMonthlySummaries(List.of(monthly));

        // trainer summary
        TrainerWorkloadSummary summary = new TrainerWorkloadSummary();
        summary.setUsername("trainer.jane");
        summary.setFirstName("Jane");
        summary.setLastName("Smith");
        summary.setIsActive(true);
        summary.setYearlySummaries(List.of(yearly));

        summaryRepository.save(summary);

    }

    @When("a trainer workload request is submitted with action ADD")
    public void a_trainer_workload_request_is_submitted_with_action_add() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest("trainer.jane", "trainer" , "jane"
                ,true , LocalDate.of(2024, 6, 1), 60, ActionType.ADD);




        submitWorkloadRequest(request,state.authHeaders());

    }

    @When("a trainer workload request is submitted with action DELETE")
    public void a_trainer_workload_request_is_submitted_with_action_delete() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest("trainer.jane", "trainer" , "jane"
                ,true , LocalDate.of(2024, 6, 1), 60, ActionType.DELETE);


        submitWorkloadRequest(request,state.authHeaders());


    }

    @When("a trainer workload request is submitted with missing required fields")
    public void a_trainer_workload_request_is_submitted_with_missing_required_fields() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(null, "" , "jane"
                ,true , LocalDate.of(2024, 6, 1), 60, ActionType.ADD);



        submitWorkloadRequest(request,state.authHeaders());

    }

    @When("an unauthenticated user submits a workload request")
    public void an_unauthenticated_user_submits_a_workload_request() {
        state.clearAuthentication();

        TrainerWorkloadRequest request = new TrainerWorkloadRequest(null, "" , "jane"
                ,true , LocalDate.of(2024, 6, 1), 60, ActionType.ADD);



        submitWorkloadRequest(request,state.jsonHeaders());



    }

    private void submitWorkloadRequest(TrainerWorkloadRequest request,HttpHeaders headers ) {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/workload/trainers",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                String.class
        );

        state.setResponse(response);
    }

    @When("the workload summary is requested for the trainer")
    public void the_workload_summary_is_requested_for_the_trainer() {

        String username = "trainer.jane";
        int year = 2024;
        int month = 6;


        requestWorkloadSummary(username, year, month,state.authHeaders());

    }

    @When("an unauthenticated user requests a workload summary")
    public void an_unauthenticated_user_requests_a_workload_summary() {
        state.clearAuthentication();

        String username = "trainer.jane";
        int year = 2024;
        int month = 6;


        requestWorkloadSummary(username, year, month,state.jsonHeaders());


    }

    @When("the workload summary is requested with invalid parameters")
    public void the_workload_summary_is_requested_with_invalid_parameters() {

        String username = "trainer.jane";
        int year = 2024;
        int month = -1;

        requestWorkloadSummary(username, year, month,state.authHeaders());


    }


    private void requestWorkloadSummary(String username, int year, int month,HttpHeaders headers) {
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/workload/trainers/"+username+"/summary?year="+year+"&month="+month,
                HttpMethod.GET,
                entity,
                String.class
        );

        state.setResponse(response);
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(int expectedStatusCode) {
        assertNotNull(state.getResponse());
        assertEquals(expectedStatusCode, state.getResponse().getStatusCode().value());
    }
}