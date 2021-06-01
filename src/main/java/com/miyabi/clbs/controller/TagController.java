package com.miyabi.clbs.controller;

import com.miyabi.clbs.pojo.Tag;
import com.miyabi.clbs.service.TagService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * com.miyabi.clbs.controller
 *
 * @Author amotomiyabi
 * @Date 2020/05/07/
 * @Description
 */
@RestController
@RequestMapping("/tags")
@ResponseBody
@CrossOrigin
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/list")
    public List<Tag> searchTags(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                @RequestParam(value = "sort", required = false, defaultValue = "1") int sort,
                                @RequestParam(value = "tag", required = false) String tag) {
        return tagService.findTagsLike(tag, page, size, sort);
    }

    @GetMapping("/list/{id}")
    public Tag searchTag(@PathVariable("id") int id) {
        return tagService.findTagById(id);
    }

    @PutMapping("/upload")
    public String addTag(@RequestBody Tag tag) {
        return tagService.addTag(tag);
    }

    @PutMapping("/uploads")
    public String addTags(@RequestBody Tag[] tags) {
        return tagService.addTags(Arrays.asList(tags));
    }
}
