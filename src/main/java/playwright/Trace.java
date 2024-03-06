package playwright;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

public class Trace {
  public static void main(String[] args){
    Playwright playwright = Playwright.create();
      Browser browser = playwright.chromium().launch();
    BrowserContext context = browser.newContext();

// Start tracing before creating / navigating a page.
    context.tracing().start(new Tracing.StartOptions()
        .setScreenshots(true)
        .setSnapshots(true)
        .setSources(true));

    Page page = context.newPage();
    //page.navigate("https://playwright.dev");

    page.navigate("https://www.wikipedia.org/");
    page.locator("input[name=\"search\"]").click();
    page.locator("input[name=\"search\"]").fill("playwright");
    page.locator("input[name=\"search\"]").press("Enter");
    assertEquals("https://en.wikipedia.org/wiki/Playwright", page.url());

    // Stop tracing and export it into a zip archive.
    context.tracing().stop(new Tracing.StopOptions()
        .setPath(Paths.get("trace2.zip")));



    //查看
    //mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace trace2.zip"





  }
}
