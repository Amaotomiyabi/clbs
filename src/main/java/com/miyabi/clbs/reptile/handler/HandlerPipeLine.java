package com.miyabi.clbs.reptile.handler;

import com.miyabi.clbs.pojo.Image;
import com.miyabi.clbs.reptile.booru.UrlHandler;
import org.jsoup.nodes.Document;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * com.miyabi.clbs.reptile.handler
 *
 * @Author amotomiyabi
 * @Date 2020/04/30/
 * @Description
 */
public class HandlerPipeLine {
    private static AtomicInteger waitTask = new AtomicInteger(0);
    private static Semaphore sp;
    private PrefixHandler prefixHandler;
    private UrlHandler urlHandler;
    private SuffixHandler suffixHandler;

    public HandlerPipeLine(PrefixHandler prefixHandler, UrlHandler urlHandler,
                           SuffixHandler suffixHandler, int max, int min) {
        this.prefixHandler = prefixHandler;
        this.urlHandler = urlHandler;
        this.suffixHandler = suffixHandler;
        sp = new Semaphore(max);
        suffixHandler.initSuffixHandler(sp);
    }

    public PrefixHandler getPrefixHandler() {
        return prefixHandler;
    }

    public void setPrefixHandler(PrefixHandler prefixHandler) {
        this.prefixHandler = prefixHandler;
    }

    public UrlHandler getUrlHandler() {
        return urlHandler;
    }

    public void setUrlHandler(UrlHandler urlHandler) {
        this.urlHandler = urlHandler;
    }

    public void run() {
        var maxImgId = urlHandler.getMaxImgId();
        while (urlHandler.getNowImgId() < maxImgId) {
            try {
                sp.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Document document = null;
            try {
                document = prefixHandler.getDom(urlHandler.getParentUrl(), 3,
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36",
                        6000);
            } catch (NullPointerException e) {
                System.err.println(e.getMessage());
            }
            String imgUrl;
            try {
                imgUrl = urlHandler.getDownloadUrl(document);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            var img = prefixHandler.process(document);
            CompletableFuture<Image> future = prefixHandler
                    .asyncDownload(imgUrl)
                    .thenApplyAsync(s -> suffixHandler.reduce(s, img));
        }
    }
}
