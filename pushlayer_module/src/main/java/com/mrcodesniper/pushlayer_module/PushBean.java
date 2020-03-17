package com.mrcodesniper.pushlayer_module;

import java.io.Serializable;

public class PushBean<T> implements Serializable {

    private String action;

    private T payload;

    public String getBizType() {
        return action;
    }

    public void setBizType(String action) {
        this.action = action;
    }

    public T getContent() {
        return payload;
    }
}
