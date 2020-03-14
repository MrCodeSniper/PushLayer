package com.mrcodesniper.pushlayer_module;

public interface MessageReceiveListener {
    /**
     * 接收到消息
     * @param msg
     */
    public void onReceivedMsg(String msg);
}
