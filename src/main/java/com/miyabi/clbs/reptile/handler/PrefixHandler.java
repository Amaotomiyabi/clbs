package com.miyabi.clbs.reptile.handler;

import com.miyabi.clbs.pojo.Image;
import com.miyabi.clbs.reptile.httpClient.HttpClientConnectionUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

/**
 * com.miyabi.clbs.reptile.handler
 *
 * @Author amotomiyabi
 * @Date 2020/04/30/
 * @Description 前置处理者
 */
public interface PrefixHandler {
    /**
     * @return
     * @Description 获取DOM文档
     */
    default Document getDom(String url, int retry, String userAgent, int timeOut)
            throws NullPointerException {
        do {
            try {
                return Jsoup.connect(url).userAgent(userAgent).timeout(timeOut).get();
            } catch (IOException e) {
                System.err.println("超时重试");
            }
        }
        while (--retry > 0);
        throw new NullPointerException("重试次数超出限制，放弃该页面 : " + url);
    }

    default Document getDom(String url) throws TimeoutException {
        return getDom(url, 3, "Mozilla/5.0 (Windows NT 10.0; WOW64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36", 30000);
    }

    /**
     * @Param DOM文档
     * @Description 解析DOM
     */
    Image process(Document document);

    /**
     * @Param 请求链接
     * @Description 下载文件
     */
    default ByteArrayOutputStream download(String imgUrl) {
        try {
            var connection = HttpClientConnectionUtil.getHttpUrlConnection(imgUrl);
            if (connection != null) {
                try (
                        var readableByteChannel = Channels.newChannel(connection.getInputStream());
                        var outputStream = new ByteArrayOutputStream();
                        var writableByteChannel = Channels.newChannel(outputStream);
                ) {
                    var buffer = ByteBuffer.allocateDirect(8192);
                    while (readableByteChannel.read(buffer) != -1) {
                        buffer.flip();
                        writableByteChannel.write(buffer);
                        buffer.clear();
                    }
                    return outputStream;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    default CompletableFuture<ByteArrayOutputStream> asyncDownload(String imgUrl) {
        return CompletableFuture.supplyAsync(() -> this.download(imgUrl));
    }

}
