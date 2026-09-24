package com.assessment.mobileautomation.driver;

import com.assessment.mobileautomation.config.CapabilityBuilder;
import com.assessment.mobileautomation.config.ConfigReader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * ThreadLocal-scoped driver so the suite can run scenarios in parallel
 * (TestNG data-provider / parallel="methods") without one thread's driver
 * bleeding into another's.
 */
public final class DriverManager {

    private static final Logger LOGGER = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void initDriver() {
        try {
            URL appiumUrl = new URL(ConfigReader.get("appium.server.url", "http://127.0.0.1:4723/"));
            AppiumDriver driver;

            if (ConfigReader.isIOS()) {
                LOGGER.info("Starting iOS session against {}", appiumUrl);
                driver = new IOSDriver(appiumUrl, CapabilityBuilder.iosOptions());
            } else {
                LOGGER.info("Starting Android session against {}", appiumUrl);
                driver = new AndroidDriver(appiumUrl, CapabilityBuilder.androidOptions());
            }
            DRIVER.set(driver);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL in config", e);
        }
    }

    public static AppiumDriver getDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("Driver not initialised for this thread - call initDriver() first");
        }
        return driver;
    }

    public static void quitDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                LOGGER.warn("Error while quitting driver", e);
            } finally {
                DRIVER.remove();
            }
        }
    }
}
