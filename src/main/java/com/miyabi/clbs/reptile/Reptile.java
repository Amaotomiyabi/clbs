package com.miyabi.clbs.reptile;

import com.miyabi.clbs.reptile.booru.UrlHandler;
import com.miyabi.clbs.reptile.concurrent.CLBSThreadFactory;
import com.miyabi.clbs.reptile.concurrent.CLBSThreadPool;
import com.miyabi.clbs.reptile.handler.HandlerPipeLine;
import com.miyabi.clbs.reptile.handler.PrefixHandler;
import com.miyabi.clbs.reptile.handler.SuffixHandler;
import com.miyabi.clbs.service.ImageService;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * com.miyabi.clbs.reptile.handler
 *
 * @Author amotomiyabi
 * @Date 2020/05/05/
 * @Description
 */
@Component
public class Reptile {
    private ThreadPoolExecutor poolExecutor;
    private HandlerPipeLine pipeLine;

    public Reptile(ImageService imageService, UrlHandler urlHandler,
                   PrefixHandler prefixHandler, SuffixHandler suffixHandler) {
        var bundle = ResourceBundle.getBundle("threadPoolInit");
        urlHandler.setNowImgId(imageService.findNowId() + 1);
        var max = Integer.parseInt(bundle.getString("maxDownload"));
        var min = Integer.parseInt(bundle.getString("minDownload"));
        pipeLine = new HandlerPipeLine(prefixHandler,
                urlHandler, suffixHandler, max, min);
    }

    public static void main(String[] args) {
    }

    public void initReptile() {
        var bundle = ResourceBundle.getBundle("threadPoolInit");
        poolExecutor = CLBSThreadPool.newExecutor(new ArrayBlockingQueue<>(
                        Integer.parseInt(bundle.getString("queueCapacity"))),
                new CLBSThreadFactory("DANBOORU", false));
        createScheduleTask(Integer.parseInt(bundle.getString("initialDelay")),
                Integer.parseInt(bundle.getString("delay")), TimeUnit.HOURS,
                Integer.parseInt(bundle.getString("taskCount")));
    }

    public void initImgId() throws TimeoutException {
        var urlHandler = pipeLine.getUrlHandler();
        var prefixHandler = pipeLine.getPrefixHandler();
        urlHandler.setMaxImgId(urlHandler.getNewMaxId(prefixHandler.getDom(urlHandler.getMasterUrl())));
    }

    public void createScheduleTask(long initialDelay, long delay, TimeUnit unit, int taskCount) {
        var stpe = Executors.newScheduledThreadPool(2);
        var cyclicBarrier = new CyclicBarrier(2);
        stpe.scheduleWithFixedDelay(() -> {
            try {
                initImgId();
                cyclicBarrier.await();
            } catch (TimeoutException e) {
                System.err.println("获取最大ID超时");
                e.printStackTrace();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }, initialDelay, delay, unit);
        stpe.scheduleWithFixedDelay(() ->
        {
            try {
                cyclicBarrier.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
            }
            IntStream.range(1, taskCount).forEach(i -> poolExecutor.execute(pipeLine::run));
            cyclicBarrier.reset();
        }, initialDelay, delay, unit);
    }
}
