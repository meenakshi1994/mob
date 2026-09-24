package com.assessment.mobileautomation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * One runner for both platforms - "android" vs "ios" is a config value
 * (ConfigReader / -Dplatform), not a different class. testng-android.xml
 * and testng-ios.xml both point at this runner and just set -Dplatform
 * differently, so glue code, tags, and reporting stay identical across
 * platforms.
 *
 * Tag filtering: -Dcucumber.filter.tags="@smoke" (also settable via the
 * cucumber.filter.tags system property picked up automatically by
 * cucumber-testng).
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.assessment.mobileautomation.stepdefinitions", "com.assessment.mobileautomation.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-report/cucumber.html",
                "json:target/cucumber-report/cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @Override
    @org.testng.annotations.DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
