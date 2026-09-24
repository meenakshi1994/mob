package com.assessment.mobileautomation.stepdefinitions;

import com.assessment.mobileautomation.pages.ProductsPage;
import io.cucumber.java.en.When;

public class LogoutSteps {

    private final ProductsPage productsPage = new ProductsPage();

    @When("I log out")
    public void i_log_out() {
        productsPage.logout();
    }
}
