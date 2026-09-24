package com.assessment.mobileautomation.stepdefinitions;

import com.assessment.mobileautomation.pages.LoginPage;
import com.assessment.mobileautomation.pages.ProductsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.testng.Assert;

public class CommonSteps {

    private final LoginPage loginPage = new LoginPage();
    private final ProductsPage productsPage = new ProductsPage();

    @Given("the app is launched on the login screen")
    public void the_app_is_launched_on_the_login_screen() {
        loginPage.open();
        Assert.assertTrue(loginPage.isDisplayedOnScreen(), "Login screen was not displayed on launch");
    }

    @Then("I should be redirected to the products screen")
    public void i_should_be_redirected_to_the_products_screen() {
        Assert.assertTrue(productsPage.isDisplayedOnScreen(), "Products screen was not displayed after login");
    }

    @Then("I should be redirected to the login screen")
    public void i_should_be_redirected_to_the_login_screen() {
        Assert.assertTrue(loginPage.isDisplayedOnScreen(), "Login screen was not displayed after logout");
    }
}
