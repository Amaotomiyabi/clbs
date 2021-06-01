package com.miyabi.clbs.dao;

import com.miyabi.clbs.pojo.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * com.miyabi.clbs.dao
 *
 * @Author amotomiyabi
 * @Date 2020/05/03/
 * @Description
 */
public interface TagRepository extends JpaRepository<Tag, Integer>, JpaSpecificationExecutor<Tag> {
}
