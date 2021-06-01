package com.miyabi.clbs.controller;

import com.miyabi.clbs.pojo.Image;
import com.miyabi.clbs.service.ImageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * com.miyabi.clbs.controller
 *
 * @Author amotomiyabi
 * @Date 2020/05/06/
 * @Description
 */
@RestController
@RequestMapping("/images")
@ResponseBody
@CrossOrigin
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/posts")
    public List<Image> getImgs(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                               @RequestParam(value = "sort", required = false, defaultValue = "1") int sort,
                               @RequestParam(value = "size", required = false, defaultValue = "18") int size,
                               @RequestParam(value = "tag", required = false) String tag) {
        return imageService.findImgsTagLike(tag, page, size, sort);
    }

    @GetMapping("/count")
    public Integer getImgCount(@RequestParam(value = "tag", required = false) String tag) {
        return imageService.searchImgCount(tag);
    }

    @GetMapping("/posts/{id}")
    public Image getImg(@PathVariable("id") int id) {
        return imageService.findImgById(id);
    }

    @PostMapping("/upload")
    public String addImg(@RequestParam("file") MultipartFile img, @RequestBody Image image) {
        imageService.addImg(img, image);
        return null;
    }

    @PostMapping("/uploads")
    public String addImgs(@RequestParam("files") MultipartFile[] imgs) {
        return String.valueOf(imageService.addImgs(imgs));
    }

    @DeleteMapping("/delete/{id}")
    public String deleteImgById(@PathVariable("id") int id) {
        return imageService.deleteImgById(id);
    }

    @DeleteMapping("/delete")
    public String deleteImgs(@RequestBody Image[] imgs) {
        return imageService.deleteImgs(Arrays.asList(imgs));
    }

    @DeleteMapping("/delete/tag")
    public String deleteImgsByTag(@RequestParam(value = "tag", required = true) String tag) {
        return imageService.deleteImgByTag(tag);
    }

    @GetMapping("/bg")
    public String getBgImg() {
        return "/static/img/index.jpg";
    }
}
