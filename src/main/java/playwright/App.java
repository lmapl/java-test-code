package playwright;

import com.microsoft.playwright.*;

public class App {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate("https://baijiahao.baidu.com/s?id=1758863070409829006&wfr=spider&for=pc");
            System.out.println(page.title());
        }
    }
}