@cart
Feature: Add to cart
  As a logged-in user
  I want to add a product to my cart
  So that I can purchase it later

  Background:
    Given the app is launched on the login screen
    And I log in with username "standard_user" and password "secret_sauce"
    And I should be redirected to the products screen

  @regression
  Scenario: Adding a product updates the cart badge count
    When I add the first product to the cart
    Then the cart badge should show "1" item
