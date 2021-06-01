package com.miyabi.clbs.reptile.handler;

import com.miyabi.clbs.dao.ImageRepository;
import com.miyabi.clbs.pojo.Image;
import com.miyabi.clbs.util.ImageFormat;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * com.miyabi.clbs.reptile.handler
 *
 * @Author amotomiyabi
 * @Date 2020/04/30/
 * @Description 持久化文件
 */
@Service
public class PersistSuffixHandler implements SuffixHandler {
    private static Semaphore sp;
    private static ConcurrentLinkedQueue<Image> queue;
    private final ImageRepository imageRepository;

    public PersistSuffixHandler(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
        queue = new ConcurrentLinkedQueue<>();
        var ses = Executors.newScheduledThreadPool(2);
        ses.scheduleWithFixedDelay(this::putInDataBase, 20, 30, TimeUnit.SECONDS);
    }

    public static Semaphore getSp() {
        return sp;
    }

    public static void setSp(Semaphore sp) {
        PersistSuffixHandler.sp = sp;
    }

    @Override
    public void initSuffixHandler(Semaphore sp) {
        PersistSuffixHandler.sp = sp;
    }

    @Override
    public Image reduce(ByteArrayOutputStream imgBos, Image img) {
        if (imgBos == null) {
            sp.release();
            return null;
        }
        byte[] imgBytes = imgBos.toByteArray();
        img.setSize(imgBytes.length);
        var path = ImageFormat.getSavePath(imgBytes);
        img.setSavePath(path);
        var file = new File(path);
        try (
                var fileChannel = new FileOutputStream(file).getChannel();
        ) {
            var buffer = ByteBuffer.wrap(imgBytes);
            fileChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        queue.offer(img);
        sp.release();
        return img;
    }

    public void putInDataBase() {
        if (queue.isEmpty()) {
            return;
        }
        var lock = new ReentrantLock();
        lock.lock();
        try {
            imageRepository.saveAll(queue);
            queue = new ConcurrentLinkedQueue<>();
        } finally {
            lock.unlock();
        }
    }


    public CompletableFuture<Image> asyncReduce(ByteArrayOutputStream imgBos, Image img) {
        return CompletableFuture.supplyAsync(() -> reduce(imgBos, img));
    }


}
