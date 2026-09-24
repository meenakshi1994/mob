@login
Feature: Login
  As a user of the Sauce Labs sample app
  I want to log in with my credentials
  So that I can access the product catalog

  Background:
    Given the app is launched on the login screen

  @smoke
  Scenario: Successful login with valid credentials
    When I log in with username "standard_user" and password "secret_sauce"
    Then I should be redirected to the products screen

  @regression
  Scenario: Unsuccessful login with invalid credentials
    When I log in with username "standard_user" and password "wrong_password"
    Then I should see an error message containing "Username and password do not match"
