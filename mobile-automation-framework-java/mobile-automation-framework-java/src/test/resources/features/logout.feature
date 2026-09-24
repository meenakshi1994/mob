@logout
Feature: Logout
  As a logged-in user
  I want to log out of the app
  So that my session is ended securely

  Background:
    Given the app is launched on the login screen
    And I log in with username "standard_user" and password "secret_sauce"
    And I should be redirected to the products screen

  @smoke
  Scenario: Successful logout
    When I log out
    Then I should be redirected to the login screen
