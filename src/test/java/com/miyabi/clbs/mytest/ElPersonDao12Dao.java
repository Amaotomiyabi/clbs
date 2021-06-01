package com.miyabi.clbs.mytest;

import com.miyabi.clbs.dao.TagRepository;
import com.miyabi.clbs.esdao.ImageESRepository;
import com.miyabi.clbs.esdao.TagESRepository;
import com.miyabi.clbs.pojo.Image;
import com.miyabi.clbs.pojo.Tag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * com.miyabi.clbs
 *
 * @Author amotomiyabi
 * @Date 2020/05/03/
 * @Description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElPersonDao12Dao {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagESRepository tagESRepository;
    @Autowired
    private ImageESRepository imageESRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void insertSome() {
        elasticsearchTemplate.createIndex(Image.class);
        elasticsearchTemplate.putMapping(Image.class);
        elasticsearchTemplate.createIndex(Tag.class);
        elasticsearchTemplate.putMapping(Tag.class);
    }
}
