package com.miyabi.clbs.reptile.httpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * com.miyabi.clbs.reptile.httpClient
 *
 * @Author amotomiyabi
 * @Date 2020/04/30/
 * @Description
 */
public class HttpClientConnectionUtil {
    /**
     * @Description 获取Http连接
     */
    public static HttpURLConnection getHttpUrlConnection(String url) throws IOException {
        try {
            return getHttpUrlConnection(new URL(url));
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static HttpURLConnection getHttpUrlConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }
}
