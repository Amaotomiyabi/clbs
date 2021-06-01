package com.miyabi.clbs.service;

import com.miyabi.clbs.pojo.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * com.miyabi.clbs.service.impl
 *
 * @Author amotomiyabi
 * @Date 2020/04/30/
 * @Description
 */
public interface ImageService {
    String addImg(MultipartFile img, Image image);

    Long addImgs(MultipartFile[] imgs);

    List<Image> findAllImgs(int page, int size, int sort);

    List<Image> findImgsTagLike(String tag, int page, int size, int sort);

    Image findImgById(int id);

    int findNowId();

    String deleteImgByTag(String tag);

    String deleteImgById(int id);

    String deleteImgs(List<Image> images);

    int searchImgCount(String tag);
}
