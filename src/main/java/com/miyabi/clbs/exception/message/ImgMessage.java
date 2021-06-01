package com.miyabi.clbs.exception.message;


/**
 * com.miyabi.clbs.exception.message
 *
 * @Author amotomiyabi
 * @Date 2020/05/07/
 * @Description
 */
public enum ImgMessage {
    /**
     * @Description 上传失败
     */
    UPLOAD_FAULT("UFAULT"),
    /**
     * @Description 上传成功
     */
    UPLOAD_SUCCESS("UOK"),
    /**
     * @Description 添加图片失败
     */
    ADD_IMG_FAULT("AIF"),
    /**
     * @Description 添加图片成功
     */
    ADD_IMG_SUCCESS("AIS"),
    /**
     * @Description 图片已经存在
     */
    IMG_EXIST("IE"),
    /**
     * @Description 更新/删除成功
     */
    UPDATE_IMG_SUCCESS("UIS"),
    /**
     * @Description 更新/删除失败
     */
    UPDATE_IMG_FAULT("UIF");


    private String msg;

    ImgMessage(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return msg;
    }
}
