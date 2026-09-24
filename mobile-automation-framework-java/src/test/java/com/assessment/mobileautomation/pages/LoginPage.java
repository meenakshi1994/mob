package com.assessment.mobileautomation.pages;

import org.openqa.selenium.By;

/**
 * Login screen of the Sauce Labs sample mobile app.
 */
public class LoginPage extends BasePage {

    private By usernameField() {
        return platformSelector("test-Username", "test-Username");
    }

    private By passwordField() {
        return platformSelector("test-Password", "test-Password");
    }

    private By loginButton() {
        return platformSelector("test-LOGIN", "test-LOGIN");
    }

    private By errorMessage() {
        return platformSelector("test-Error message", "test-Error message");
    }

    public void open() {
        // The app launches directly onto the login screen for a signed-out
        // user; nothing to navigate to. This method exists so step
        // definitions read naturally and a future "relaunch app" step has a
        // single place to change.
        waitForDisplayed(usernameField());
    }

    public void login(String username, String password) {
        waitAndSetValue(usernameField(), username);
        waitAndSetValue(passwordField(), password);
        waitAndClick(loginButton());
    }

    public String getErrorText() {
        return getText(errorMessage());
    }

    public boolean isDisplayedOnScreen() {
        return isDisplayed(usernameField());
    }
}
