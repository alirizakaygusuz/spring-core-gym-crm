Feature: Trainer Management
  As a Trainer
  I want to manage my profile

  Background:
    Given the application is running

  @trainer @register @smoke
  Scenario: Successful trainer registration
    Given a new trainer registration request with valid data
    When the trainer submits the registration request
    Then the response status code should be 200
    And the response contains the trainer's username and password


  @trainer @register @negative
  Scenario: Trainer registration fails with missing fields
    Given a new trainer registration request with missing required fields
    When the trainer submits the registration request
    Then the response status code should be 400

  @trainer @profile @smoke
  Scenario: Get trainer profile successfully
    Given the trainer is logged in with valid credentials
    When the trainer requests their profile
    Then the response status code should be 200
    And the response contains the trainer's profile information


  @trainer @profile @negative @nfr
  Scenario: Get another trainer's profile is forbidden
    Given the trainer is logged in with valid credentials
    When the trainer requests another trainer's profile
    Then the response status code should be 403

  @trainer @profile @negative @nfr
  Scenario: Get trainer profile fails without authentication
    Given the trainer is not logged in
    When the unauthenticated trainer attempts to access a trainer profile
    Then the response status code should be 401

  @trainer @update @smoke
  Scenario: Successful update of trainer profile
    Given the trainer is logged in with valid credentials
    When the trainer updates their profile with valid data
    Then the response status code should be 200
    And the response contains the updated trainer information

  @trainer @update @negative @nfr
  Scenario: Update another trainer's profile is forbidden
    Given the trainer is logged in with valid credentials
    When the trainer attempts to update another trainer's profile
    Then the response status code should be 403


  @trainer @update @negative @nfr
  Scenario: Update trainer profile fails without authentication
    Given the trainer is not logged in
    When the unauthenticated trainer attempts to update a trainer profile
    Then the response status code should be 401

  @trainer @update @negative
  Scenario: Update trainer profile fails with invalid data
    Given the trainer is logged in with valid credentials
    When the trainer attempts to update their profile with invalid data
    Then the response status code should be 400


  @trainer @status @smoke
  Scenario: Successful update of trainer status
    Given the trainer is logged in with valid credentials
    When the trainer updates their status
    Then the response status code should be 200


  @trainer @status @negative
  Scenario: Throw Exception when updating trainer status with same status value
    Given the trainer is logged in with valid credentials
    When the trainer attempts to update their status with the same value
    Then the response status code should be 400


  @trainer @status @negative @nfr
  Scenario: Update another trainer's status is forbidden
    Given the trainer is logged in with valid credentials
    When the trainer attempts to update another trainer's status
    Then the response status code should be 403

  @trainer @status @negative @nfr
  Scenario: Update trainer status fails without authentication
    Given the trainer is not logged in
    When the unauthenticated trainer attempts to update their status
    Then the response status code should be 401


  @trainer @trainings @smoke
  Scenario: Successful retrieval of trainer's trainings
    Given the trainer is logged in with valid credentials
    When the trainer requests their trainings
    Then the response status code should be 200

  @trainer @trainings @negative @nfr
  Scenario: Get trainer's trainings fails without authentication
    Given the trainer is not logged in
    When the unauthenticated trainer attempts to access their trainings
    Then the response status code should be 401

  @trainer @trainings @negative @nfr
  Scenario: Get another trainer's trainings is forbidden
    Given the trainer is logged in with valid credentials
    When the trainer attempts to access another trainer's trainings
    Then the response status code should be 403

