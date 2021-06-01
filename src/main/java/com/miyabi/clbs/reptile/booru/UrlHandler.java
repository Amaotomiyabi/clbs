package com.miyabi.clbs.reptile.booru;

import org.jsoup.nodes.Document;

/**
 * com.miyabi.clbs.reptile.handler
 *
 * @Author amotomiyabi
 * @Date 2020/04/30/
 * @Description
 */
public interface UrlHandler {

    int getNowImgId();

    void setNowImgId(int i);

    String getMasterUrl();

    int getNewMaxId(Document document);

    int getMaxImgId();

    void setMaxImgId(int i);

    String getDownloadUrl(Document document) throws Exception;

    String getParentUrl();
}
