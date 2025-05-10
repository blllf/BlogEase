package com.blllf.blogease.enums;

public enum LikedStatusEnum {

    LIKE(1,"点赞"),
    UNLIKE(0,"取消") ;

    private Integer code;
    private String msg;

    LikedStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "LikedStatusEnum{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
