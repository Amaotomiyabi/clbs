package com.miyabi.clbs.exception.message;

/**
 * com.miyabi.clbs.exception.message
 *
 * @Author amotomiyabi
 * @Date 2020/05/08/
 * @Description
 */
public enum TagMessage {
    /**
     * @Description 添加标签成功
     */
    ADD_TAG_SUCCESS("ATS"),
    /**
     * @Description 添加标签失败
     */
    ADD_TAG_FAULT("ATF"),
    /**
     * @Description Tag已存在
     */
    TAG_EXIST("TE");
    private String msg;

    TagMessage(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return msg;
    }
}
