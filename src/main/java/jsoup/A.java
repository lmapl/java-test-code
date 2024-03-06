package jsoup;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class A {
  public static void main(String[] args) throws IOException {
    String html = "<div><p>Lorem ipsum.</p>";
    Document document = parseHtmlToDocument(html);
    Document document2 = parseHtmlFragmentToDocument(html);
    Document document3= urlToDocument("http://java.mobiletrain.org/jiaocheng/45862.html");

    //从 Document 对象中提取目标对象
    //方法一：doc.getElementByXXX()
    Element content = document3.getElementById("content");
    Elements links = document3.getElementsByTag("a");

    //方法二： CSS selectors
    // a with href
    Elements link2s = document3.select("a[href]");
    // image
    Elements pngs = document3.select("img[src$=.png]");
    // class
    Elements masthead = document3.select("div.article");

    String html2 = masthead.html();

    Document doc = Jsoup.connect("http://jsoup.org").get();

    Element link = doc.select("a").first();
    // == "/"
    String relHref = link.attr("href");
    // "http://jsoup.org/"
    String absHref = link.attr("abs:href");


    //修改
    //单个
    Element element = document3.select("div.article").first();
    element.attr("title", "jsoup")
        .addClass("round-box");

    //批量
    Elements elements = document3.select("div.article");
    elements.attr("title", "jsoup")
        .addClass("round-box");

    //Element.html(String html) 替换原文(标签内的内容)
    //Element.prepend(String first) 前面插入
    //Element.append(String last) 后面插入
    
    //Element.wrap(String around) 把对象插入到参数html的中间
    Element div = document3.select("div").first();
    div.html("<p>lorem ipsum</p>");

    div.prepend("<p>First</p>");
    div.append("<p>Last</p>");

    // <span>One</span>
    Element span = document3.select("span").first();
    span.wrap("<li><a href='http://example.com/'></a></li>");
// now: <li><a href="http://example.com"><span>One</span></a></li>

  String am=  span.html();





    document = null;




  }


  /**
   * html 解析转换为 Document
   */
  private static Document parseHtmlToDocument(String html) {
    return Jsoup.parse(html);
    //return Jsoup.parse(html,"https://www.baidu.com");
  }

  /**
   * html片段 解析转换为 Document，Jsoup会补全
   */
  private static Document parseHtmlFragmentToDocument(String html){
    return Jsoup.parseBodyFragment(html);
  }

  /**
   * html片段 解析转换为 Document，Jsoup会补全
   */
  private static Document urlToDocument(String url){
    try {
      return Jsoup.connect(url)
          /*.cookie()
          .data()
          .header()
          .timeout()
          .method()*/
          .get();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

}
