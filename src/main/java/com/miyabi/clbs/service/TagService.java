package com.miyabi.clbs.service;

import com.miyabi.clbs.pojo.Tag;

import java.util.List;

/**
 * com.miyabi.clbs.service.impl
 *
 * @Author amotomiyabi
 * @Date 2020/04/30/
 * @Description
 */
public interface TagService {
    String addTag(Tag tag);

    String addTags(List<Tag> tags);

    List<Tag> findAllTags(int page, int size, int sort);

    List<Tag> findTagsLike(String tag, int page, int size, int sort);

    Tag findTagById(int id);
}
