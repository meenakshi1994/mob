package com.assessment.mobileautomation.pages;

import com.assessment.mobileautomation.config.ConfigReader;
import com.assessment.mobileautomation.driver.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Base Page Object.
 * Centralises the two things that make Android/iOS handling clean and
 * scalable rather than if/else scattered through every page class:
 *   1. platformSelector(android, ios) - pick the right locator per platform
 *      from a single call site.
 *   2. Explicit-wait helpers - every interaction is synchronised, nothing
 *      relies on a fixed Thread.sleep().
 */
public abstract class BasePage {

    protected static final Logger LOGGER = LogManager.getLogger(BasePage.class);
    private static final int DEFAULT_TIMEOUT_SECONDS = ConfigReader.getInt("explicit.wait.seconds", 15);

    protected AppiumDriver driver() {
        return DriverManager.getDriver();
    }

    protected boolean isAndroid() {
        return ConfigReader.isAndroid();
    }

    protected boolean isIOS() {
        return ConfigReader.isIOS();
    }

    /**
     * Resolve a cross-platform locator by By, given an Android and an iOS
     * accessibility-id ("~name" style, kept consistent with the JS/WDIO
     * version of this framework).
     */
    protected By platformSelector(String androidAccessibilityId, String iosAccessibilityId) {
        String id = isAndroid() ? androidAccessibilityId : iosAccessibilityId;
        return By.xpath(accessibilityIdToXPath(id));
    }

    private String accessibilityIdToXPath(String accessibilityId) {
        return String.format("//*[@content-desc=\"%s\" or @name=\"%s\" or @label=\"%s\"]",
                accessibilityId, accessibilityId, accessibilityId);
    }

    protected WebDriverWait wait() {
        return new WebDriverWait(driver(), Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
    }

    protected WebElement waitForDisplayed(By locator) {
        return wait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void waitAndClick(By locator) {
        wait().until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void waitAndSetValue(By locator, String value) {
        WebElement element = waitForDisplayed(locator);
        element.clear();
        element.sendKeys(value);
    }

    protected String getText(By locator) {
        return waitForDisplayed(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            new WebDriverWait(driver(), Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Polls a condition instead of a fixed sleep - used where UI state lags
     * an action by an animation frame or two (e.g. a badge counter).
     */
    public void waitUntilStable(BooleanSupplier condition, int timeoutSeconds, String message) {
        long deadline = System.currentTimeMillis() + (timeoutSeconds * 1000L);
        RuntimeException lastError = null;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (condition.getAsBoolean()) {
                    return;
                }
            } catch (RuntimeException e) {
                lastError = e;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        throw new AssertionError(message, lastError);
    }

    protected <T> T retry(Supplier<T> action, int retries) {
        RuntimeException lastError = null;
        for (int attempt = 0; attempt <= retries; attempt++) {
            try {
                return action.get();
            } catch (RuntimeException e) {
                lastError = e;
                LOGGER.warn("Retrying action, attempt {}/{}: {}", attempt + 1, retries, e.getMessage());
            }
        }
        throw lastError;
    }
}
