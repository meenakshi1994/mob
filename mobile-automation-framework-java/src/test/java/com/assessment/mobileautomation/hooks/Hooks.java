package com.assessment.mobileautomation.hooks;

import com.assessment.mobileautomation.driver.DriverManager;
import com.assessment.mobileautomation.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Hooks {

    private static final Logger LOGGER = LogManager.getLogger(Hooks.class);

    @Before
    public void setUp(Scenario scenario) {
        LOGGER.info("Starting scenario: {}", scenario.getName());
        DriverManager.initDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                LOGGER.error("Scenario failed: {}", scenario.getName());
                byte[] screenshot = ScreenshotUtil.captureAndSave(scenario.getName());
                if (screenshot != null) {
                    scenario.attach(screenshot, "image/png", "Failure screenshot");
                }
            }
        } finally {
            DriverManager.quitDriver();
        }
    }
}
