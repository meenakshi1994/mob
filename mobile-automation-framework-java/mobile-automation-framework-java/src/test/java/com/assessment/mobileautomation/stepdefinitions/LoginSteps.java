package com.assessment.mobileautomation.stepdefinitions;

import com.assessment.mobileautomation.pages.LoginPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class LoginSteps {

    private final LoginPage loginPage = new LoginPage();

    @When("I log in with username {string} and password {string}")
    public void i_log_in_with_username_and_password(String username, String password) {
        loginPage.login(username, password);
    }

    @Then("I should see an error message containing {string}")
    public void i_should_see_an_error_message_containing(String expectedText) {
        String actualText = loginPage.getErrorText();
        Assert.assertTrue(actualText.contains(expectedText),
                "Expected error message to contain \"" + expectedText + "\" but was \"" + actualText + "\"");
    }
}
