package com.assessment.mobileautomation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Config-driven execution: loads config/config.properties (shared) then
 * layers config/{platform}.properties on top (android.properties or
 * ios.properties), so device/app/platform-version details never live in
 * test code. Any key can also be overridden from the command line
 * (-Dkey=value) or an environment variable of the same name (upper-cased,
 * dots -> underscores) - useful for CI, where a device farm injects its own
 * values without editing files.
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();
    private static String platform;

    static {
        load();
    }

    private ConfigReader() {
    }

    private static void load() {
        platform = System.getProperty("platform", System.getenv().getOrDefault("PLATFORM", "android")).toLowerCase();

        loadFile("config/config.properties");
        loadFile("config/" + platform + ".properties");
    }

    private static void loadFile(String resourcePath) {
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Config file not found on classpath: " + resourcePath);
            }
            Properties fileProps = new Properties();
            fileProps.load(in);
            PROPERTIES.putAll(fileProps);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + resourcePath, e);
        }
    }

    public static String getPlatform() {
        return platform;
    }

    public static boolean isAndroid() {
        return "android".equalsIgnoreCase(platform);
    }

    public static boolean isIOS() {
        return "ios".equalsIgnoreCase(platform);
    }

    /**
     * Resolves a value with priority: system property > environment variable > properties file.
     */
    public static String get(String key) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isEmpty()) {
            return sysProp;
        }
        String envKey = key.toUpperCase().replace('.', '_');
        String envVar = System.getenv(envKey);
        if (envVar != null && !envVar.isEmpty()) {
            return envVar;
        }
        return PROPERTIES.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        return (value == null || value.isEmpty()) ? defaultValue : Integer.parseInt(value);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return (value == null || value.isEmpty()) ? defaultValue : Boolean.parseBoolean(value);
    }
}
