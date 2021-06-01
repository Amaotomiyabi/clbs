package com.miyabi.clbs.pojo;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;

/**
 * com.miyabi.clbs.pojo
 *
 * @Author amotomiyabi
 * @Date 2020/04/27/
 * @Description 图片
 */
@Data
@Entity
@Table(name = "image")
@Document(indexName = "image")
public class Image {
    /**
     * @Description 图片ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i_id")
    @org.springframework.data.annotation.Id
    @Field(type = FieldType.Integer, name = "i_id")
    private int id = -1;
    /**
     * @Description 图片大小
     */
    @Column(name = "i_size")
    @Field(type = FieldType.Integer, name = "i_size")
    private int size;
    /**
     * @Description 图片保存路径
     */
    @Column(name = "i_url")
    @Field(type = FieldType.Keyword, name = "i_url")
    private String savePath;
    /**
     * @Description 图片标签
     */
    @Column(name = "i_tags")
    @Field(type = FieldType.Text, name = "i_tags", analyzer = "ik_max_word")
    private String tags;
    /**
     * @Description 图片作者
     */
    @Column(name = "i_author")
    @Field(type = FieldType.Text, name = "i_author", analyzer = "ik_max_word")
    private String author;
    /**
     * @Description 图片出处
     */
    @Column(name = "i_from")
    @Field(type = FieldType.Text, name = "i_from", analyzer = "ik_max_word")
    private String from;
    /**
     * @Description 图片人物名
     */
    @Column(name = "i_chara")
    @Field(type = FieldType.Text, name = "i_chara", analyzer = "ik_max_word")
    private String characters;
    /**
     * @Description 图片源
     */
    @Column(name = "i_source")
    @Field(type = FieldType.Keyword, name = "i_source")
    private String source;
    /**
     * @Description isR18
     */
    @Column(name = "i_sexy")
    @Field(type = FieldType.Integer, name = "i_sexy")
    private int sexy;
    /**
     * @Description 点击量
     */
    @Column(name = "i_clicks")
    @Field(type = FieldType.Integer, name = "i_clicks")
    private int clicks;
    /**
     * @Description 来源ID
     */
    @Column(name = "i_source_id")
    @Field(type = FieldType.Long, name = "i_source_id")
    private int sourceId;

    public Image(int size, String savePath) {
        this.size = size;
        this.savePath = savePath;
    }

    public Image(int id) {
        this.id = id;
    }

    public Image() {
    }

    public int getSourceId() {
        return sourceId;
    }

    public Image setSourceId(int sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    public int getClicks() {
        return clicks;
    }

    public Image setClicks(int clicks) {
        this.clicks = clicks;
        return this;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", size=" + size +
                ", savePath='" + savePath + '\'' +
                ", tags='" + tags + '\'' +
                ", author='" + author + '\'' +
                ", from='" + from + '\'' +
                ", source='" + source + '\'' +
                ", characters='" + characters + '\'' +
                ", sexy=" + sexy +
                '}';
    }

    public int getSexy() {
        return sexy;
    }

    public Image setSexy(int sexy) {
        this.sexy = sexy;
        return this;
    }


    public String getCharacters() {
        return characters;
    }

    public Image setCharacters(String characters) {
        this.characters = characters;
        return this;
    }

    /**
     * create by : amotomiyabi
     * description : 添加描述(标签）
     * create time : 2020/4/29
     */

    public int getId() {
        return id;
    }

    public Image setId(int id) {
        this.id = id;
        return this;
    }

    public int getSize() {
        return size;
    }

    public Image setSize(int size) {
        this.size = size;
        return this;
    }

    public String getSavePath() {
        return savePath;
    }

    public Image setSavePath(String savePath) {
        this.savePath = savePath;
        return this;
    }

    public String getTags() {
        return tags;
    }

    public Image setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Image setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public Image setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getSource() {
        return source;
    }

    public Image setSource(String source) {
        this.source = source;
        return this;
    }
}
