package com.miyabi.clbs.util;

import org.jsoup.nodes.Document;

/**
 * com.miyabi.clbs.util
 *
 * @Author amotomiyabi
 * @Date 2020/04/29/
 * @Description
 */
public class MyDocument {
    private int fromId;
    private Document document;


    public MyDocument(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }
}
