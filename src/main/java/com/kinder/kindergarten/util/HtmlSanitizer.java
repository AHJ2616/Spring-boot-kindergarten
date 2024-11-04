package com.kinder.kindergarten.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlSanitizer {

  public static String sanitize(String input) {
    if (input == null) {
      return null;
    }

    // Summernote에서 사용하는 태그와 속성들을 허용하는 Safelist 작성
    Safelist safelist = Safelist.relaxed()
            .addTags("div", "span", "p", "br", "hr", "h1", "h2", "h3", "h4", "h5", "h6",
                    "table", "thead", "tbody", "tr", "td", "th",
                    "strong", "em", "s", "u", "pre", "code", "blockquote",
                    "ol", "ul", "li", "a", "img")
            .addAttributes(":all", "style", "class")
            .addAttributes("a", "href", "target")
            .addAttributes("img", "src", "alt", "width", "height")
            .addProtocols("img", "src", "http", "https", "data")
            .addProtocols("a", "href", "http", "https", "mailto");

    // style 속성에서 허용할 CSS 속성 설정
    safelist.addEnforcedAttribute("a", "rel", "nofollow");

    return Jsoup.clean(input, safelist);
  }
}
