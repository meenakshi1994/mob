package com.assessment.mobileautomation.stepdefinitions;

import com.assessment.mobileautomation.pages.ProductsPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class CartSteps {

    private final ProductsPage productsPage = new ProductsPage();

    @When("I add the first product to the cart")
    public void i_add_the_first_product_to_the_cart() {
        productsPage.addFirstProductToCart();
    }

    @Then("the cart badge should show {string} item")
    public void the_cart_badge_should_show_item(String expectedCount) {
        int expected = Integer.parseInt(expectedCount);
        // Badge text can lag one animation frame behind the tap on some
        // devices; poll briefly instead of asserting immediately - a
        // concrete case of the flakiness handling described in the README.
        productsPage.waitUntilStable(
                () -> productsPage.getCartBadgeCount() == expected,
                5,
                "Cart badge count did not update in time");
        Assert.assertEquals(productsPage.getCartBadgeCount(), expected);
    }
}
