package com.assessment.mobileautomation.pages;

import org.openqa.selenium.By;

/**
 * Products (catalog) screen - lands here after a successful login.
 * Also owns the side-menu -> Logout flow and the "add to cart" flow used as
 * the additional functional-flow scenario.
 */
public class ProductsPage extends BasePage {

    private By screenTitle() {
        return platformSelector("test-PRODUCTS", "test-PRODUCTS");
    }

    private By menuButton() {
        return platformSelector("test-Menu", "test-Menu");
    }

    private By logoutOption() {
        return platformSelector("test-LOGOUT", "test-LOGOUT");
    }

    private By firstProductAddToCartButton() {
        String xpath = isAndroid()
                ? "(//android.widget.Button[contains(@content-desc,\"ADD TO CART\")])[1]"
                : "(//XCUIElementTypeButton[contains(@label,\"ADD TO CART\")])[1]";
        return By.xpath(xpath);
    }

    private By cartBadge() {
        return platformSelector("test-Cart badge", "test-Cart badge");
    }

    public boolean isDisplayedOnScreen() {
        return isDisplayed(screenTitle());
    }

    public void addFirstProductToCart() {
        waitAndClick(firstProductAddToCartButton());
    }

    public int getCartBadgeCount() {
        return Integer.parseInt(getText(cartBadge()));
    }

    public void logout() {
        waitAndClick(menuButton());
        waitAndClick(logoutOption());
    }
}
