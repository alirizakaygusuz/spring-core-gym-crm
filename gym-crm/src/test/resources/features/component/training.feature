Feature: Training Management
  As a user
  I want to manage training sessions and view training types

  Background:
    Given the application is running

  @training @create @smoke
  Scenario: Successful creation of a training session
    Given the trainer is logged in with valid credentials
    When the trainer creates a new training session with valid data
    Then the response status code should be 200

  @training @create @negative
  Scenario: Creation of a training session fails with missing fields
    Given the trainer is logged in with valid credentials
    When the trainer creates a new training session with missing required fields
    Then the response status code should be 400

  @training @create @negative @nfr
  Scenario: Creation of a training session fails without authentication
    Given the trainer is not logged in
    When the unauthenticated trainer attempts to create a new training session
    Then the response status code should be 401


  @training @create @negative @nfr
  Scenario: Creation of a training session for another trainer is forbidden
    Given the trainer is logged in with valid credentials
    When the trainer creates a training session for another trainer
    Then the response status code should be 403

  @training  @smoke
  Scenario:Successful retrieval of training types
    When requests the list of training types
    Then the response status code should be 200
    And the response contains a list of training types
