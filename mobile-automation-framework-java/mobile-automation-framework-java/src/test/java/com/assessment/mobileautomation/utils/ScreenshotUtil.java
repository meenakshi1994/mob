package com.assessment.mobileautomation.utils;

import com.assessment.mobileautomation.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

public final class ScreenshotUtil {

    private static final Logger LOGGER = LogManager.getLogger(ScreenshotUtil.class);
    private static final Path SCREENSHOT_DIR = Paths.get("target", "screenshots");

    private ScreenshotUtil() {
    }

    /**
     * Takes a screenshot, writes it to target/screenshots/ (kept even if the
     * CI job discards target/ otherwise, since this is the artifact you'd
     * attach to a bug report) and returns the raw bytes so the caller can
     * also attach it inline to the Cucumber/Allure report.
     */
    public static byte[] captureAndSave(String label) {
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            byte[] bytes = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);

            String safeName = label.replaceAll("[^a-zA-Z0-9-_]", "_");
            File file = SCREENSHOT_DIR.resolve(safeName + "-" + Instant.now().toEpochMilli() + ".png").toFile();
            Files.write(file.toPath(), bytes);
            LOGGER.info("Screenshot saved: {}", file.getAbsolutePath());
            return bytes;
        } catch (IOException | IllegalStateException e) {
            LOGGER.error("Failed to capture failure screenshot", e);
            return null;
        }
    }
}
