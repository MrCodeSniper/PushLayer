package com.mrcodesniper.pushlayer_module;

public interface MessageReceiveListener<T> {
    /**
     * 接收到消息
     * @param data
     */
    public void onReceivedMsg(T data);
}
