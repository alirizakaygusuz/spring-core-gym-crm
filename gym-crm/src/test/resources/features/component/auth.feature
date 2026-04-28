Feature: Authentication
  As a User
  I want to login to the system
  So that I can access protected features

  Background:
    Given the application is running

  @login @smoke
  Scenario: Successful login
    Given a seeded user exists in the system
    When the user logs in with valid credentials
    Then the response status code should be 200
    And the response contains a JWT token

  @login @negative
  Scenario: Login fails with invalid password
    Given a seeded user exists in the system
    When the user logs in with invalid password
    Then the response status code should be 401

  @login @negative
  Scenario: Login fails with non-existent user
    When an unknown user attempts to login
    Then the response status code should be 401

  @login @negative @nfr
  Scenario: Login blocked after 3 failed attempts
    Given a seeded user exists in the system
    When the user attempts to login 3 times with wrong password
    And the user tries to login once more
    Then the response status code should be 429