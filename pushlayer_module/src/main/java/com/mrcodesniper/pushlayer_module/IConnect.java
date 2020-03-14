package com.mrcodesniper.pushlayer_module;

import android.content.Context;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;

public interface IConnect {

    /**
     * 连接推送服务器
     */
    void doClientConnection(Context context, ConnectCallback listener);

}
