package com.assessment.mobileautomation.config;

import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.io.File;
import java.time.Duration;

/**
 * Turns ConfigReader values into Appium capability objects. This is the
 * single place that knows how Android and iOS capabilities differ - page
 * objects and step definitions never build capabilities themselves.
 */
public final class CapabilityBuilder {

    private CapabilityBuilder() {
    }

    public static UiAutomator2Options androidOptions() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName(ConfigReader.get("device.name"));
        options.setPlatformVersion(ConfigReader.get("platform.version"));
        options.setAutomationName(ConfigReader.get("automation.name", "UiAutomator2"));
        options.setApp(resolveAppPath(ConfigReader.get("app.path")));
        options.setAutoGrantPermissions(ConfigReader.getBoolean("auto.grant.permissions", true));
        options.setNewCommandTimeout(Duration.ofSeconds(120));
        return options;
    }

    public static XCUITestOptions iosOptions() {
        XCUITestOptions options = new XCUITestOptions();
        options.setDeviceName(ConfigReader.get("device.name"));
        options.setPlatformVersion(ConfigReader.get("platform.version"));
        options.setAutomationName(ConfigReader.get("automation.name", "XCUITest"));
        options.setApp(resolveAppPath(ConfigReader.get("app.path")));
        String udid = ConfigReader.get("udid");
        if (udid != null && !udid.isEmpty()) {
            options.setUdid(udid);
        }
        options.setAutoAcceptAlerts(ConfigReader.getBoolean("auto.accept.alerts", true));
        options.setNewCommandTimeout(Duration.ofSeconds(120));
        return options;
    }

    private static String resolveAppPath(String configuredPath) {
        File file = new File(configuredPath);
        return file.getAbsolutePath();
    }
}
