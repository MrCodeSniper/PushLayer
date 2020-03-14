package com.mrcodesniper.pushlayer_module;

public interface ConnectCallback {

    /**
     * 连接成功
     */
    void onConnectSuccess();

    /**
     * 连接失败
     * @param t
     */
    void onConnectFail(Throwable t);


}
