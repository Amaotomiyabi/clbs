package com.miyabi.clbs.reptile.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * com.miyabi.clbs.reptile.concurrent
 *
 * @Author amotomiyabi
 * @Date 2020/04/30/
 * @Description
 */
public class CLBSThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNum = new AtomicInteger(1);
    private final String prefix;
    private final boolean daemon;

    public CLBSThreadFactory(String prefix, boolean daemon) {
        this.prefix = prefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        var name = prefix + ":" + threadNum.getAndIncrement();
        var thread = new Thread(runnable, name);
        thread.setDaemon(daemon);
        return thread;
    }
}
