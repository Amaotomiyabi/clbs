package com.miyabi.clbs.service.impl;

import com.miyabi.clbs.dao.ImageRepository;
import com.miyabi.clbs.esdao.ImageESRepository;
import com.miyabi.clbs.exception.message.ImgMessage;
import com.miyabi.clbs.pojo.Image;
import com.miyabi.clbs.service.ImageService;
import com.miyabi.clbs.util.ImageAnalysis;
import com.miyabi.clbs.util.ImageFormat;
import com.miyabi.clbs.util.Sorter;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * com.miyabi.clbs.service.impl
 *
 * @Author amotomiyabi
 * @Date 2020/05/06/
 * @Description
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final ImageESRepository imageESRepository;

    public ImageServiceImpl(ImageRepository imageRepository, ImageESRepository imageESRepository) {
        this.imageRepository = imageRepository;
        this.imageESRepository = imageESRepository;
    }

    @Override
    public String addImg(MultipartFile img, Image image) {
        String pathStr;
        Path path;
        if (img.isEmpty()) {
            return ImgMessage.UPLOAD_FAULT.toString();
        }
        try (
                InputStream is = img.getInputStream();) {
            if (ImageAnalysis.imgCompare(is)) {
                return ImgMessage.IMG_EXIST.toString();
            }
            pathStr = ImageFormat.getSavePath(Objects.requireNonNull(img.getContentType()));
            path = Path.of(pathStr);
            img.transferTo(path);
        } catch (IOException e) {
            e.printStackTrace();
            return ImgMessage.UPLOAD_FAULT.toString();
        }
        List<String> tags = ImageAnalysis.getTags(pathStr);
        image.setTags(String.join(",", tags))
                .setSavePath(pathStr)
                .setSexy(ImageAnalysis.isSexy(tags.get(tags.size() - 1)))
                .setSize((int) img.getSize());
        return (imageRepository.save(image).getId() == -1 ? ImgMessage.UPLOAD_FAULT : ImgMessage.UPLOAD_SUCCESS).toString();
    }

    @Override
    public Long addImgs(MultipartFile[] imgs) {
        var imgList = Arrays.stream(imgs).filter(img -> !img.isEmpty()).collect(Collectors.toList());
        ArrayList<Image> images = new ArrayList<>();
        for (MultipartFile entry : imgList) {
            String pathStr = ImageFormat.getSavePath(Objects.requireNonNull(entry.getContentType()));
            Path path = Path.of(pathStr);
            try (InputStream is = entry.getInputStream();) {
                if (ImageAnalysis.imgCompare(entry.getInputStream())) {
                    continue;
                }
                entry.transferTo(path);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            images.add(new Image().setSavePath(pathStr)
                    .setSize((int) entry.getSize()));
        }
        List<List<String>> tagsList = ImageAnalysis
                .getTagLists(images.stream().map(Image::getSavePath).collect(Collectors.toList()));
        for (int i = 0; i < images.size(); i++) {
            var image = images.get(i);
            var tags = tagsList.get(i);
            image.setTags(String.join(",", tags))
                    .setSexy(ImageAnalysis.isSexy(tags.get(tags.size() - 1)));
        }
        return imageRepository.saveAll(images)
                .stream()
                .filter(s -> s.getId() != -1)
                .count();
    }

    @Override
    public List<Image> findAllImgs(int page, int size, int sort) {
        return imageESRepository.findAll(PageRequest.of(--page, size, Sorter.getSort(sort, "id"))).toList();
    }

    @Override
    public List<Image> findImgsTagLike(String tag, int page, int size, int sort) {
        return tag == null ? this.findAllImgs(page, size, sort) : imageESRepository
                .search(QueryBuilders.matchQuery("i_tags", tag),
                        PageRequest.of(--page, size, Sorter.getSort(sort, "id"))).toList();
    }

    @Override
    public Image findImgById(int id) {
        return imageESRepository.findById(id).orElse(null);
    }

    @Override
    public int searchImgCount(String tag) {
        if (tag == null) {
            return (int) imageESRepository.count();
        }
        int i = 0;
        var list = imageESRepository.search(QueryBuilders.matchQuery("i_tags", tag)).iterator();
        while (list.hasNext()) {
            list.next();
            i++;
        }
        return i;
    }

    @Override
    public int findNowId() {
        return imageRepository.queryMaxId();
    }

    @Override
    public String deleteImgByTag(String tag) {
        imageRepository.deleteImagesByTag(tag);
        imageESRepository.deleteAll(imageESRepository.search(QueryBuilders.matchQuery("i_tags", tag)));
        return ImgMessage.UPDATE_IMG_SUCCESS.toString();
    }

    @Override
    public String deleteImgById(int id) {
        imageRepository.deleteById(id);
        imageESRepository.deleteById(id);
        return ImgMessage.UPDATE_IMG_SUCCESS.toString();

    }

    @Override
    public String deleteImgs(List<Image> images) {
        imageRepository.deleteInBatch(images);
        imageESRepository.deleteAll(images);
        return ImgMessage.UPDATE_IMG_SUCCESS.toString();
    }
}
