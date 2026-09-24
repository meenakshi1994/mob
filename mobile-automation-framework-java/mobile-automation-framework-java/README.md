# Mobile UI Automation Framework (Java + Appium + Cucumber + TestNG)

Cross-platform (Android & iOS) UI automation framework for the **Sauce Labs
sample mobile app**, built to the brief in `Technical_assessment.docx`.

## Why this stack

- **Appium Java client + Selenium 4**: one driver layer for both Android
  (UiAutomator2) and iOS (XCUITest) - avoids maintaining two frameworks.
- **Cucumber (BDD) + TestNG**: scenarios in `src/test/resources/features/*.feature`
  are readable by non-engineers and double as living documentation; tags
  (`@smoke`, `@regression`) drive selective execution; TestNG runs the
  generated scenarios and gives parallel-execution support out of the box.
- **Page Object Model**: one page class per screen (`pages/`); step
  definitions never contain a raw selector.
- **Allure**: step-level HTML report with screenshots attached to failures.

## Prerequisites

- JDK 11+
- Maven 3.8+
- Appium 2.x server running locally (`appium` on the CLI, or via Appium
  Desktop) with the `uiautomator2` and `xcuitest` drivers installed:
  ```bash
  appium driver install uiautomator2
  appium driver install xcuitest   # macOS only
  ```
- Android: Android Studio + an emulator/AVD, `ANDROID_HOME` set
- iOS: Xcode + a simulator (macOS only)
- The Sauce Labs sample app builds, placed under `./apps/`:
  https://github.com/saucelabs/sample-app-mobile/releases
  (`.apk` for Android; for iOS simulator use the `.app` bundle zipped,
  a real `.ipa` is only needed for a physical device)
- Allure CLI (optional, for `mvn allure:serve`):
  https://allurereport.org/docs/gettingstarted-installation/

## Setup

```bash
mvn -q clean install -DskipTests
```

Device/app details live in `src/test/resources/config/` — edit
`android.properties` / `ios.properties` (or override any key from the
command line, see below) rather than touching test code.

Start Appium in one terminal:

```bash
appium
```

## Running tests

```bash
# Android - full suite
mvn test -Dplatform=android

# Android - smoke only
mvn test -Dplatform=android -Dcucumber.filter.tags="@smoke"

# Android - regression only
mvn test -Dplatform=android -Dcucumber.filter.tags="@regression"

# iOS
mvn test -Dplatform=ios
mvn test -Dplatform=ios -Dcucumber.filter.tags="@smoke"

# Any custom tag expression
mvn test -Dplatform=android -Dcucumber.filter.tags="@smoke and not @flaky"
```

`-Dplatform` selects both which `testng-<platform>.xml` suite Surefire runs
(see `pom.xml`) and which `config/<platform>.properties` file
`ConfigReader` layers on top of `config/config.properties`.

**API tests**: this assessment's scope was UI-only, so no API suite is
included. If added, the pattern to follow is a sibling `src/test/java/.../api`
package with its own REST-assured step definitions and a separate TestNG
suite, kept out of the mobile capabilities/driver entirely so the two
suites don't share state.

## Reports

- Cucumber's own HTML/JSON report: `target/cucumber-report/cucumber.html`
- Allure (richer, step-level, with attached screenshots):
  ```bash
  mvn allure:report   # builds target/site/allure-maven-plugin
  mvn allure:serve    # builds and opens it
  ```
- Raw Allure results: `target/allure-results/`
- Failure screenshots: `target/screenshots/` (also attached inline to the
  Allure/Cucumber report via the `@After` hook in `hooks/Hooks.java`)

## Configuration approach

Nothing device-, path-, or credential-specific is hard-coded. Values load
from `config/config.properties` (shared) then `config/<platform>.properties`
via `ConfigReader`, which also lets any key be overridden from the command
line (`-Dkey=value`) or an environment variable — useful for CI/device
farms, where the runner injects its own values without editing files.
`CapabilityBuilder` is the single place that turns those values into
Appium `UiAutomator2Options` / `XCUITestOptions` objects; page objects and
step definitions never build capabilities themselves.

## Framework structure

```
src/test/java/com/assessment/mobileautomation/
  config/            ConfigReader (properties + env/-D overrides), CapabilityBuilder
  driver/            DriverManager - ThreadLocal<AppiumDriver> lifecycle
  pages/             BasePage (platformSelector + explicit waits) + one class per screen
  hooks/              Before/After hooks: driver init/teardown, failure screenshots
  stepdefinitions/    glue code only - no selectors, no capability logic
  runners/            single TestRunner (AbstractTestNGCucumberTests)
  utils/              ScreenshotUtil
src/test/resources/
  features/           Gherkin scenarios, tagged @smoke / @regression
  config/             config.properties, android.properties, ios.properties
  testng-android.xml, testng-ios.xml    per-platform TestNG suites, same TestRunner
  log4j2.xml
```

### Cross-platform handling

`BasePage.platformSelector(androidId, iosId)` resolves an accessibility-id
based locator for whichever platform `ConfigReader` says is active. Step
definitions and features are platform-agnostic; only the page-object layer
knows Android and iOS exist. Where behaviour genuinely diverges (e.g. the
add-to-cart button's underlying XPath), that branches inside the page
object method, never in a step definition.

### Locator & sync strategy

- Locators prefer accessibility-id (stable across OS versions/UI tweaks)
  over raw XPath; XPath is used only where the app exposes no
  accessibility id (see `firstProductAddToCartButton()`).
- All interactions go through `BasePage`'s `waitForDisplayed` /
  `waitAndClick` / `waitAndSetValue`, backed by Selenium's
  `WebDriverWait` + `ExpectedConditions` rather than a fixed sleep.
- `waitUntilStable()` polls a condition for UI state that lags an
  animation frame behind an action (see the cart-badge assertion in
  `CartSteps`) instead of adding a blind wait.

## Flakiness & reliability notes

- **Animations / lagging UI state** (e.g. a badge count that updates a beat
  after the tap): polled with `waitUntilStable()` rather than asserting on
  the very next line.
- **Session left in a bad state between scenarios**: `Hooks` starts a fresh
  `AppiumDriver` in `@Before` and quits it in `@After` for every scenario,
  so no scenario depends on the app state a previous one left behind.
- **Silent failures mid-step** (crash, permission dialog): the `@After`
  hook captures a screenshot and attaches it to the report on any failed
  scenario, and `DriverManager.quitDriver()` is wrapped so a failed quit
  never masks the original test failure.
- **Known trade-off**: starting a brand-new driver session per scenario is
  simple and reliable but adds real time (app install/launch) to a large
  suite; for a bigger regression pack this would move to a shared session
  per feature with an explicit "navigate back to a known screen" reset,
  keeping full session resets only for scenarios that mutate login state.

## How AI was used in this assessment

- Scaffolding the initial project structure (Maven layout, config split,
  BasePage, Cucumber/TestNG wiring) to match the four pillars in the brief
  (POM, config-driven execution, failure handling, BDD/tagging) without
  missing one.
- Drafting the cross-platform `platformSelector()` pattern as a single,
  reusable way to avoid `if (isAndroid) {...} else {...}` scattered through
  step definitions.
- Porting an equivalent WebdriverIO/JS version of this same framework to
  Java/Appium/TestNG on request, keeping the scenarios, page structure, and
  flakiness-handling approach consistent across both.
- Writing this README structure so setup/run/config/decisions are easy for
  a reviewer to verify quickly.
- All locators are placeholders based on the public Sauce Labs sample app's
  known accessibility ids and should be verified/adjusted against the
  actual app build before running against a real device/emulator.

## Notes

- No proprietary or employer code/data is included; all test data is the
  Sauce Labs sample app's published demo credentials (`standard_user` /
  `secret_sauce`).
- `apps/`, `target/` are gitignored (see `.gitignore`).
