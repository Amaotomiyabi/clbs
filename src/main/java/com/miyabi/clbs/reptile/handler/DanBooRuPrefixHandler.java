package com.miyabi.clbs.reptile.handler;

import com.miyabi.clbs.pojo.Image;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

/**
 * com.miyabi.clbs.reptile.handler
 *
 * @Author amotomiyabi
 * @Date 2020/04/30/
 * @Description
 */
@Service
public class DanBooRuPrefixHandler implements PrefixHandler {
    @Override
    public Image process(Document document) {
        var image = new Image();
        Element elementTemp;
        if ((elementTemp = document.selectFirst("img#image")) != null) {
            image.setTags(elementTemp.attr("data-tags").replaceAll(" ", ","));
        } else {
            return null;
        }
        if ((elementTemp = document.selectFirst("#post-info-source > a:nth-child(1)")) != null) {
            image.setSource(elementTemp.attr("href"));
        }
        if ((elementTemp = document.selectFirst("a[itemprop=author]")) != null) {
            image.setAuthor(elementTemp.text());
        }
        if ((elementTemp = document.selectFirst("ul.character-tag-list")) != null) {
            var sb = new StringBuilder();
            for (var element1 : elementTemp.select("a.search-tag")) {
                sb.append(element1.text());
                sb.append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            image.setCharacters(sb.toString());
        }
        if ((elementTemp = document.selectFirst("#tag-list > ul.copyright-tag-list > li > a.search-tag")) != null) {
            image.setFrom(elementTemp.text());
        }
        image.setSourceId(Integer.parseInt(document
                .selectFirst(".image-container")
                .attr("data-id")));
        return image;
    }
}
