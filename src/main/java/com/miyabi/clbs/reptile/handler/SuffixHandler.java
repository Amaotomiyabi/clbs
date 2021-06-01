package com.miyabi.clbs.reptile.handler;

import com.miyabi.clbs.pojo.Image;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Semaphore;


/**
 * com.miyabi.clbs.reptile.handler
 *
 * @Author amotomiyabi
 * @Date 2020/04/30/
 * @Description
 */
public interface SuffixHandler {
    /**
     * @Description 处理文件
     */
    Image reduce(ByteArrayOutputStream imgBos, Image img);

    void initSuffixHandler(Semaphore sp);
}
