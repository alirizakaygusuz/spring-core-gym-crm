Feature: Trainer Workload Management
  As a user
  I want to manage trainer workload data

  Background:
    Given the application is running

  @workload @process @add @smoke
  Scenario: Successful workload processing with ADD action
    Given the user is authenticated
    When a trainer workload request is submitted with action ADD
    Then the response status code should be 200

  @workload @process @delete @smoke
  Scenario: Successful workload processing with DELETE action
    Given the user is authenticated
    And a trainer workload entry exists
    When a trainer workload request is submitted with action DELETE
    Then the response status code should be 200

  @workload @process  @negative
  Scenario: Workload processing fails with missing required fields
    Given the user is authenticated
    When a trainer workload request is submitted with missing required fields
    Then the response status code should be 400

  @workload @process @negative @nfr
  Scenario: Workload processing fails without authentication
    When an unauthenticated user submits a workload request
    Then the response status code should be 401

  @workload @summary @smoke
  Scenario: Successful retrieval of workload summary
    Given the user is authenticated
    And a trainer workload entry exists
    When the workload summary is requested for the trainer
    Then the response status code should be 200

  @workload @summary @negative @nfr
  Scenario: Workload summary fails without authentication
    When an unauthenticated user requests a workload summary
    Then the response status code should be 401

  @workload @summary @negative
  Scenario: Workload summary fails with invalid parameters
    Given the user is authenticated
    When the workload summary is requested with invalid parameters
    Then the response status code should be 400