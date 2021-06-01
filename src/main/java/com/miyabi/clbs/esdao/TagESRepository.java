package com.miyabi.clbs.esdao;

import com.miyabi.clbs.pojo.Tag;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * com.miyabi.clbs.dao.es
 *
 * @Author amotomiyabi
 * @Date 2020/05/05/
 * @Description
 */
public interface TagESRepository extends ElasticsearchRepository<Tag, Integer> {
}
