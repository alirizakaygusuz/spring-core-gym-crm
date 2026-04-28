Feature: Training and Trainee Integration with Workload Service
  As a system
  I want gym-crm actions to update workload-service via JMS

  Background:
    Given the application is running

  @integration @jms-add
  Scenario: Creating a training updates trainer workload via JMS
    Given a trainee is registered
    And a trainer is registered
    And the trainer is logged in
    When a 60-minute training is created
    Then the response status code should be 200
    And the trainer workload should be updated with 60 minutes


  @integration @jms-delete
  Scenario: Deleting a trainee profile removes related trainer workload via JMS
    Given a trainee is registered
    And a trainer is registered
    And the trainee is logged in
    And the trainer is logged in
    And a 60-minute training exists between the trainee and trainer
    And the trainer workload should be 60 minutes for the current month
    When the trainee deletes their profile
    Then the response status code should be 200
    And the trainer workload summary should not be found for the current month