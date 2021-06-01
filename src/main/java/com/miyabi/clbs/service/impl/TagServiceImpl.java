package com.miyabi.clbs.service.impl;

import com.miyabi.clbs.dao.TagRepository;
import com.miyabi.clbs.esdao.TagESRepository;
import com.miyabi.clbs.exception.message.TagMessage;
import com.miyabi.clbs.pojo.Tag;
import com.miyabi.clbs.service.TagService;
import com.miyabi.clbs.util.Sorter;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * com.miyabi.clbs.service.impl
 *
 * @Author amotomiyabi
 * @Date 2020/05/06/
 * @Description
 */
@Service
public class TagServiceImpl implements TagService {

    private final TagESRepository tagESRepository;
    private final TagRepository tagRepository;

    public TagServiceImpl(TagESRepository tagESRepository, TagRepository tagRepository) {
        this.tagESRepository = tagESRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public String addTag(Tag tag) {
        return (tagRepository.save(tag).getId() == -1 ? TagMessage.ADD_TAG_FAULT : TagMessage.ADD_TAG_SUCCESS).toString();
    }

    @Override
    public String addTags(List<Tag> tags) {
        return String.valueOf(tagRepository.saveAll(tags).stream().filter(s -> s.getId() != -1).count());
    }

    @Override
    public List<Tag> findAllTags(int page, int size, int sort) {
        return tagESRepository.findAll(PageRequest.of(page, size, Sorter.getSort(sort, "id"))).toList();
    }

    @Override
    public List<Tag> findTagsLike(String tag, int page, int size, int sort) {
        page--;
        return tag != null ? tagESRepository
                .search(QueryBuilders.prefixQuery("t_content", tag),
                        PageRequest.of(page, size, Sorter.getSort(sort, "id"))).toList() : this.findAllTags(page, size, sort);
    }

    @Override
    public Tag findTagById(int id) {
        return tagESRepository.findById(id).orElse(null);
    }
}
