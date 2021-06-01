package com.miyabi.clbs.util;

import com.miyabi.clbs.pojo.Image;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * com.miyabi.clbs.util
 *
 * @Author amotomiyabi
 * @Date 2020/04/27/
 * @Description 获取URL的工具类
 */
public class DownloadTool {
    private final static String DANBOORU = "https://danbooru.donmai.us/posts/";
    private final ThreadPoolExecutor threadPool;
    private final BlockingQueue<MyDocument> doms;
    private final BlockingQueue<Image> imageBlockingQueue;
    private final CountDownLatch latch = new CountDownLatch(1);
    private final AtomicInteger nowDanBooRuId = new AtomicInteger(3886000);
    private final LinkedList<MyDocument> danBooFaultToGetUrls;
    private final LinkedList<Image> faultToDownloadImgs;
    private final AtomicInteger danBoMaxId;
    private String nowUrl;
    private volatile int sum = 0;

    public DownloadTool() {
        threadPool = initThreadPool();
        doms = new ArrayBlockingQueue<>(2);
        imageBlockingQueue = new ArrayBlockingQueue<>(20);
        danBooFaultToGetUrls = new LinkedList<>();
        faultToDownloadImgs = new LinkedList<>();
        danBoMaxId = new AtomicInteger(0);
    }

    /**
     * create by : amotomiyabi
     * description : 初始化线程池
     * create time : 2020/4/28
     */
    private static ThreadPoolExecutor initThreadPool() {
        ResourceBundle bundle = ResourceBundle.getBundle("threadPoolInit");
        BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(Integer.parseInt(bundle.getString("queueCapacity")));
        return new ThreadPoolExecutor(Integer.parseInt(bundle.getString("corePoolSize")),
                Integer.parseInt(bundle.getString("maximumPoolSize")),
                Integer.parseInt(bundle.getString("keepAliveTime"))
                , TimeUnit.SECONDS, queue, new MyThreadFactory("Get Img "));
    }

    public static void main(String[] args) {
        DownloadTool downloadTool = new DownloadTool();
        downloadTool.setNowUrl(DANBOORU);
        downloadTool.getImage();
    }

    public String getNowUrl() {
        return nowUrl;
    }

    public void setNowUrl(String nowUrl) {
        this.nowUrl = nowUrl;
    }

    public String getImageParentUrl(String url, int id) {
        return url + id;
    }

    public int getNowDanBooRuId() {
        return nowDanBooRuId.getAndIncrement();
    }

    /**
     * create by : amotomiyabi
     * description : 获得网页DOM文档
     * create time : 2020/4/28
     */
    public MyDocument getDom(String url, int retry) throws TimeoutException {
        Connection connection = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36").timeout(5000);
        do {
            try {
                return new MyDocument(connection.get());
            } catch (IOException e) {
                retry--;
                System.err.println("超时重试");
            }
        }
        while (--retry > 0);
        throw new TimeoutException("重试次数超出限制，放弃该页面");
    }

    private void putDomInDos(String webSiteUrl, int id) throws InterruptedException {
        MyDocument document;
        try {
            document = getDom(getImageParentUrl(webSiteUrl, id), 3);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return;
        }
        document.setFromId(id);
        doms.put(document);
    }

    /**
     * create by : amotomiyabi
     * description : 判断图片是否存在
     * create time : 2020/4/29
     */
    public boolean imgExist(int id) {
        if (id < nowDanBooRuId.get()) {
            return true;
        }
        return false;
    }

    /**
     * create by : amotomiyabi
     * description : Danboo网站获取图片
     * create time : 2020/4/28
     */
    @SuppressWarnings("AlibabaUndefineMagicConstant")
    public void getImageUrlDanboo(MyDocument myDom) throws InterruptedException {
        Document dom = myDom.getDocument();
        Image image;
        if (dom == null) {
            return;
        }
        try {
            String imageUrl = dom
                    .getElementById("post-option-download")
                    .selectFirst("a[href]").attr("abs:href");
            if (imageBlockingQueue.size() >= 15) {
                latch.await();
            }
            image = new Image();
            String strTags = dom.getElementById("content")
                    .selectFirst("img#image")
                    .attr("data-tags");
            image.setTags(strTags);
            Element elementTemp;
            if ((elementTemp = dom.getElementById("post-info-source").selectFirst("a[href]")) != null) {
                image.setSource(elementTemp.attr("href"));
            }
            if ((elementTemp = dom.selectFirst("a[itemprop=author]")) != null) {
                image.setAuthor(elementTemp.text());
            }
            if ((elementTemp = dom.selectFirst("ul.character-tag-list")) != null) {
                StringBuilder sb = new StringBuilder();
                for (Element element1 : elementTemp.select("a.search-tag")) {
                    sb.append(element1.text());
                    sb.append(" ");
                }
                image.setCharacters(sb.toString());
            }
            if ((elementTemp = dom.selectFirst("ul.copyright-tag-list")) != null) {
                image.setFrom(elementTemp.selectFirst("a.search-tag").text());
            }
            System.out.println(Thread.currentThread().getName() + "  当前图片下载链接 : " + imageUrl);
            imageBlockingQueue.put(image);
        } catch (NullPointerException e) {
            System.err.println("无法获取下载地址" + myDom.getFromId());
            e.printStackTrace();
        }
    }

    /**
     * create by : amotomiyabi
     * description : 下载图片
     * create time : 2020/4/28
     */
    public ByteArrayOutputStream downloadImage(String imageUrl) throws IOException {
        URL url;
        HttpURLConnection connection;
        try {
            url = new URL(imageUrl);
        } catch (MalformedURLException e) {
            return null;
        }
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            try (
                    ReadableByteChannel readableByteChannel = Channels.newChannel(connection.getInputStream());
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    WritableByteChannel writableByteChannel = Channels.newChannel(outputStream);
            ) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(8192);
                while (readableByteChannel.read(buffer) != -1) {
                    buffer.flip();
                    writableByteChannel.write(buffer);
                    buffer.clear();
                }
                return outputStream;
            }
        } catch (IOException e) {
            throw new IOException();
        }
    }

    /**
     * create by : amotomiyabi
     * description : 保存图片
     * create time : 2020/4/28
     */
    public synchronized Image saveImage(ByteArrayOutputStream outputStream, Image img) {
        byte[] imgBytes = outputStream.toByteArray();
        img.setSize(imgBytes.length);
        String path = "C:\\Users\\amotomiyabi\\Desktop\\testImage\\" + UUID.randomUUID().toString() +
                ImageFormat.judgeFormat(Arrays.copyOf(imgBytes, 3));
        img.setSavePath(path);
        File file = new File(path);
        try (
                FileChannel fileChannel = new FileOutputStream(file).getChannel();
        ) {
            sum++;
            ByteBuffer buffer = ByteBuffer.wrap(imgBytes);
            fileChannel.write(buffer);
            if (imageBlockingQueue.size() < 5) {
                latch.countDown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    /**
     * create by : amotomiyabi
     * description : 获取网站最大ID
     * create time : 2020/4/30
     */
    public int getMaxId() {
        return danBoMaxId.get();
    }

    /**
     * create by : amotomiyabi
     * description : 分析主站
     * create time : 2020/4/30
     */
    public int getDanBoMaxId(MyDocument myDocument) {
        Document document = myDocument.getDocument();
        return Integer.parseInt(
                document.selectFirst("article").
                        selectFirst("a[href]").attr("href").
                        substring(7));
    }

    public void getImage() {
        ScheduledExecutorService stpe = Executors.newScheduledThreadPool(2);
        stpe.scheduleWithFixedDelay(new ScheduleGetImageUrlFaultProcessor(), 0, 12, TimeUnit.HOURS);
        CountDownLatch latch = new CountDownLatch(1);
        stpe.scheduleWithFixedDelay(new GetMaxIdTask(latch), 0, 1, TimeUnit.MINUTES);
        stpe.scheduleWithFixedDelay(new CreteGetDomTask(latch), 0, 1, TimeUnit.MINUTES);
        for (int i = 0; i < 2; i++) {
            threadPool.execute(new GetImageUrlTask());
        }
        for (int i = 0; i < 7; i++) {
            threadPool.execute(new SaveImageTask());
        }
    }

    /**
     * create by : amotomiyabi
     * description : 自定义线程工厂
     * create time : 2020/4/27
     */
    private static class MyThreadFactory implements ThreadFactory {
        String dec;
        int count = 0;

        public MyThreadFactory(String dec) {
            this.dec = dec;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName(dec + count++);
            return thread;
        }
    }

    /**
     * @Author amotomiyabi
     * @Date 2020/4/30
     * @Description 定时创建任务
     */
    private class CreteGetDomTask implements Runnable {
        CountDownLatch latch;

        public CreteGetDomTask(CountDownLatch countDownLatch) {
            latch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 4; i++) {
                threadPool.execute(new GetDomTask());
            }
        }
    }

    /**
     * @Author amotomiyabi
     * @Date 2020/4/30
     * @Description 定时获取最大ID任务
     */
    private class GetMaxIdTask implements Runnable {
        CountDownLatch latch;

        public GetMaxIdTask(CountDownLatch countDownLatch) {
            latch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                danBoMaxId.set(getDanBoMaxId(getDom(getNowUrl(), 3)));
                latch.countDown();
            } catch (TimeoutException e) {
                e.printStackTrace();
                System.err.println("获取最大ID失败");
            }
        }
    }

    /**
     * @Author amotomiyabi
     * @Date 2020/4/28
     * @Description 获取图片URL的任务
     */
    private class GetDomTask implements Runnable {

        @Override
        public void run() {
            int maxId = getMaxId();
            int id;
            do {
                String webSiteUrl = getNowUrl();
                id = getNowDanBooRuId();
                try {
                    putDomInDos(webSiteUrl, id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (id < maxId);
            threadPool.isShutdown();
        }
    }

    /**
     * create by : amotomiyabi
     * description : 重新获取之前获取失败的图片
     * create time : 2020/4/29
     */
    private class ScheduleGetImageUrlFaultProcessor implements Runnable {

        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            while (danBooFaultToGetUrls.size() > 0) {
                MyDocument myDocument = danBooFaultToGetUrls.remove();
                try {
                    getImageUrlDanboo(myDocument);
                    System.out.println("处理完成");
                } catch (InterruptedException e) {
                    danBooFaultToGetUrls.add(myDocument);
                    e.printStackTrace();
                }
            }
            System.out.println("当前无错误需要");
        }
    }

    private class GetImageUrlTask implements Runnable {

        @Override
        public void run() {
            while (doms.size() > 0 || threadPool.isTerminated()) {
                MyDocument myDocument = null;
                try {
                    myDocument = doms.take();
                    getImageUrlDanboo(myDocument);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @Author amotomiyabi
     * @Date 2020/4/28
     * @Description 自定义任务类_保存图片
     */
    private class SaveImageTask implements Runnable {
        @Override
        public void run() {
            Image img = null;
            while (true) {
                try {
                    img = imageBlockingQueue.take();
                    saveImage(downloadImage("dsad"), img);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    System.err.println("链接无效");
                } catch (IOException e) {
                    System.err.println("下载/保存失败");
                    faultToDownloadImgs.add(img);
                }
                System.out.println("保存图片执行完成,第" + sum + "张");
            }
        }
    }
}

