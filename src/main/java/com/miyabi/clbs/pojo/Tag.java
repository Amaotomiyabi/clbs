package com.miyabi.clbs.pojo;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;

/**
 * com.miyabi.clbs.pojo
 *
 * @Author amotomiyabi
 * @Date 2020/04/27/
 * @Description 图片描述
 */
@Entity
@Table(name = "tag")
@Document(indexName = "tag")
public class Tag {
    /**
     * @Description 标签ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "t_id")
    @org.springframework.data.annotation.Id
    @Field(type = FieldType.Integer, name = "t_id")
    private int id = -1;
    /**
     * @Description 具体标签内容
     */
    @Column(name = "t_content")
    @Field(type = FieldType.Text, name = "t_content", analyzer = "ik_max_word")
    private String content;

    @Column(name = "t_clicks")
    @Field(type = FieldType.Integer, name = "t_clicks")
    private int clicks;

    public Tag() {

    }

    public Tag(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public Tag(String content) {
        this.content = content;
    }

    public int getClicks() {
        return clicks;
    }

    public Tag setClicks(int clicks) {
        this.clicks = clicks;
        return this;
    }

    public int getId() {
        return id;
    }

    public Tag setId(int id) {
        this.id = id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Tag setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
