package playwright;

// 1
import com.deque.html.axecore.playwright.*;
import com.deque.html.axecore.results.AxeResults;

import org.junit.jupiter.api.*;
import com.microsoft.playwright.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;

public class HomepageTests {
  @Test // 2
  void shouldNotHaveAutomaticallyDetectableAccessibilityIssues() throws Exception {
    Playwright playwright = Playwright.create();
    Browser browser = playwright.chromium().launch();
    BrowserContext context = browser.newContext();
    Page page = context.newPage();

    // 3
    page.navigate("https://your-site.com/");

    // 4
    AxeResults accessibilityScanResults = new AxeBuilder(page).analyze();

    // 5
    assertEquals(Collections.emptyList(), accessibilityScanResults.getViolations());
  }

  @Test
  void navigationMenuFlyoutShouldNotHaveAutomaticallyDetectableAccessibilityViolations() throws Exception {
    Playwright playwright = Playwright.create();
    Browser browser = playwright.chromium().launch();
    //Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(50));

    BrowserContext context = browser.newContext();
    Page page = context.newPage();
    page.navigate("https://your-site.com/");

    page.locator("button[class=\"loginbutton\"]").click();

    // It is important to waitFor() the page to be in the desired
    // state *before* running analyze(). Otherwise, axe might not
    // find all the elements your test expects it to scan.
    page.locator("#navigation-menu-flyout").waitFor();

    AxeResults accessibilityScanResults = new AxeBuilder(page)
        .include(Arrays.asList("#navigation-menu-flyout"))
        .analyze();

    assertEquals(Collections.emptyList(), accessibilityScanResults.getViolations());
  }
}