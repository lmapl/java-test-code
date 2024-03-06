package playwright;


import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.*;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.regex.Pattern;


public class A2 {
 static Pattern pattern = Pattern.compile("Playwright");

  public static void main(String[] args) {
    try (Playwright playwright = Playwright.create()) {
      Browser browser = playwright.chromium().launch();
      Page page = browser.newPage();
      page.navigate("http://playwright.dev");

      // Expect a title "to contain" a substring.
      assertThat(page).hasTitle(pattern);

      // create a locator
      Locator getStarted = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Get Started"));

      // Expect an attribute "to be strictly equal" to the value.
      assertThat(getStarted).hasAttribute("href", "/docs/intro");

      Locator getStarted2 = page.locator("text=TypeScript");
      String a = getStarted2.getAttribute("href");

      // Click the get started link.
      // 模拟点击页面跳转
      getStarted.click();

      // Expects page to have a heading with the name of Installation.
      // 跳转后新页面内的内容
      assertThat(page.getByRole(AriaRole.HEADING,
          new Page.GetByRoleOptions().setName("Installation"))).isVisible();



    }
  }
}
