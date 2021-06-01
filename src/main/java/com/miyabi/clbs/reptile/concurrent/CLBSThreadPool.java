package com.miyabi.clbs.reptile.concurrent;

import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * com.miyabi.clbs.reptile.concurrent
 *
 * @Author amotomiyabi
 * @Date 2020/04/30/
 * @Description
 */
@Component
public class CLBSThreadPool {
    public static ThreadPoolExecutor newExecutor(BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
        var bundle = ResourceBundle.getBundle("threadPoolInit");
        return new ThreadPoolExecutor(Integer.parseInt(bundle.getString("corePoolSize")),
                Integer.parseInt(bundle.getString("maximumPoolSize")),
                Integer.parseInt(bundle.getString("keepAliveTime"))
                , TimeUnit.SECONDS, blockingQueue, threadFactory);
    }
}
