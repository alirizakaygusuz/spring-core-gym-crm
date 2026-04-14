Feature: Trainee Management
  As a Trainee
  I want to manage my profile

  Background:
    Given the application is running


  @trainee @register @smoke
  Scenario: Successful trainee registration
    Given a new trainee registration request with valid data
    When the trainee submits the registration request
    Then the response status code should be 200
    And the response contains the trainee's username and password


  @trainee @register @negative
  Scenario: Trainee registration fails with missing fields
    Given a new trainee registration request with missing required fields
    When the trainee submits the registration request
    Then the response status code should be 400

  @trainee @profile @smoke
  Scenario: Get trainee profile successfully
    Given the trainee is logged in with valid credentials
    When the trainee requests their profile
    Then the response status code should be 200
    And the response contains the trainee's profile information

  @trainee @profile @negative @nfr
  Scenario: Get another trainee's profile is forbidden
    Given the trainee is logged in with valid credentials
    When the trainee requests another trainee's profile
    Then the response status code should be 403

  @trainee @profile @negative @nfr
  Scenario: Get trainee profile fails without authentication
    Given the trainee is not logged in
    When the unauthenticated trainee attempts to access a trainee profile
    Then the response status code should be 401

  @trainee @update @smoke
  Scenario: Successful update of trainee profile
    Given the trainee is logged in with valid credentials
    When the trainee updates their profile with valid data
    Then the response status code should be 200
    And the response contains the updated trainee information

  @trainee @update @negative @nfr
  Scenario: Update another trainee's profile is forbidden
    Given the trainee is logged in with valid credentials
    When the trainee attempts to update another trainee's profile
    Then the response status code should be 403

  @trainee @update @negative @nfr
  Scenario: Update trainee profile fails without authentication
    Given the trainee is not logged in
    When the unauthenticated trainee attempts to update a trainee profile
    Then the response status code should be 401

  @trainee @update @negative
  Scenario: Update trainee profile fails with invalid data
    Given the trainee is logged in with valid credentials
    When the trainee updates their profile with invalid data
    Then the response status code should be 400

  @trainee @delete @smoke
  Scenario: Successful deletion of trainee profile
    Given a new trainee is registered and logged in with valid credentials
    When the trainee deletes their profile
    Then the response status code should be 200

  @trainee @delete @negative @nfr
  Scenario: Deletion another trainee's profile is forbidden
    Given the trainee is logged in with valid credentials
    When the trainee attempts to delete another trainee's profile
    Then the response status code should be 403

  @trainee @delete @negative @nfr
  Scenario: Deletion of trainee profile fails without authentication
    Given the trainee is not logged in
    When the unauthenticated trainee attempts to delete a trainee profile
    Then the response status code should be 401


  @trainee @status @smoke
  Scenario: Successful update of trainee status
    Given the trainee is logged in with valid credentials
    When the trainee updates their status
    Then the response status code should be 200

  @trainee @status @negative
  Scenario: Throw Exception when updating trainee status with same status value
    Given the trainee is logged in with valid credentials
    When the trainee attempts to update their status with the same value
    Then the response status code should be 400

  @trainee @status @negative @nfr
  Scenario: Update another trainee's status is forbidden
    Given the trainee is logged in with valid credentials
    When the trainee attempts to update another trainee's status
    Then the response status code should be 403

  @trainee @status @negative @nfr
  Scenario: Update trainee status fails without authentication
    Given the trainee is not logged in
    When the unauthenticated trainee attempts to update their status
    Then the response status code should be 401


  @trainee @not-assigned-trainers @smoke
  Scenario: Successful getting of not assigned trainers
    Given the trainee is logged in with valid credentials
    When the trainee requests the list of not assigned trainers
    Then the response status code should be 200

  @trainee @not-assigned-trainers @negative @nfr
  Scenario: Get another trainee's not assigned trainers is forbidden
    Given the trainee is logged in with valid credentials
    When the trainee requests another trainee's not assigned active trainers
    Then the response status code should be 403


  @trainee @not-assigned-trainers @negative @nfr
  Scenario: Get not assigned active trainers fails without authentication
    Given the trainee is not logged in
    When the unauthenticated trainee attempts to access the list of not assigned active trainers
    Then the response status code should be 401

  @trainee @update-trainer-list @smoke
  Scenario: Successful update of trainee's trainer list
    Given the trainee is logged in with valid credentials
    When the trainee updates their trainer list with valid trainer usernames
    Then the response status code should be 200

  @trainee @update-trainer-list @negative @nfr
  Scenario: Update another trainee's trainer list is forbidden
    Given the trainee is logged in with valid credentials
    When the trainee attempts to update another trainee's trainer list
    Then the response status code should be 403

  @trainee @update-trainer-list @negative @nfr
  Scenario: Update trainee's trainer list fails without authentication
    Given the trainee is not logged in
    When the unauthenticated trainee attempts to update trainee's trainer list
    Then the response status code should be 401


  @trainee @trainings @smoke
  Scenario: Successful retrieval of trainee's trainings
    Given the trainee is logged in with valid credentials
    When the trainee requests their trainings
    Then the response status code should be 200

  @trainee @trainings @negative @nfr
  Scenario: Get another trainee's trainings is forbidden
    Given the trainee is logged in with valid credentials
    When the trainee requests another trainee's trainings
    Then the response status code should be 403

  @trainee @trainings @negative @nfr
  Scenario: Get trainee trainings fails without authentication
    Given the trainee is not logged in
    When the unauthenticated trainee attempts to access trainee trainings
    Then the response status code should be 401