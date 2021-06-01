package com.miyabi.clbs.reptile.booru;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * com.miyabi.clbs.reptile.handler
 *
 * @Author amotomiyabi
 * @Date 2020/04/30/
 * @Description
 */
@Service
public class DanbooRuUrlHandler implements UrlHandler {
    private static final AtomicInteger maxImgId = new AtomicInteger(1);
    private static final AtomicInteger nowImgId = new AtomicInteger(1);
    private static final String DANBOORU_URL = "https://danbooru.donmai.us/posts/";

    private DanbooRuUrlHandler() {
    }

    public static DanbooRuUrlHandler getHandler() {
        return SingleTonHandler.getINSTANCE();
    }

    @Override
    public int getNowImgId() {
        return nowImgId.get();
    }

    @Override
    public void setNowImgId(int i) {
        nowImgId.set(i);
    }

    @Override
    public String getMasterUrl() {
        return DANBOORU_URL;
    }

    @Override
    public int getNewMaxId(Document document) {
        return Integer.parseInt(
                document.selectFirst("article").
                        selectFirst("a[href]").attr("href").
                        substring(7));
    }

    @Override
    public int getMaxImgId() {
        return maxImgId.get();
    }

    @Override
    public void setMaxImgId(int i) {
        maxImgId.set(i);
    }

    @Override
    public String getDownloadUrl(Document document) throws Exception {
        try {
            return document
                    .getElementById("post-option-download")
                    .selectFirst("a[href]")
                    .attr("abs:href");
        } catch (NullPointerException e) {
            throw new Exception("无法获取下载链接");
        }
    }

    @Override
    public String getParentUrl() {
        return DANBOORU_URL + nowImgId.getAndIncrement();
    }

    private static class SingleTonHandler {
        private static DanbooRuUrlHandler INSTANCE = new DanbooRuUrlHandler();

        public static DanbooRuUrlHandler getINSTANCE() {
            return INSTANCE;
        }
    }
}
