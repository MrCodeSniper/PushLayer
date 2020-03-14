package com.mrcodesniper.pushlayer_module;

import java.io.Serializable;

public class PushBean<T> implements Serializable {

    private String bizType;

    private T content;

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public T getContent() {
        return content;
    }
}
