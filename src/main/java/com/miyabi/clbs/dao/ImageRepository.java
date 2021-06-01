package com.miyabi.clbs.dao;

import com.miyabi.clbs.pojo.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * com.miyabi.clbs.dao.mapper
 *
 * @Author amotomiyabi
 * @Date 2020/04/30/
 * @Description
 */
public interface ImageRepository extends JpaRepository<Image, Integer>, JpaSpecificationExecutor<Image> {

    @Query(value = "SELECT i_source_id FROM image ORDER BY i_source_id ASC LIMIT 1", nativeQuery = true)
    int queryMaxId();

    @Query(value = "DELETE FROM image WHERE MATCH(i_tags) AGAINST('?*' IN BOOLEAN MODE ) ", nativeQuery = true)
    void deleteImagesByTag(String tag);

}
