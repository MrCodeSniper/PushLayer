package com.mrcodesniper.pushlayer;

import android.os.Build;

public interface AppPushConfig {

    String HOST = "tcp://192.168.2.104:61613";

    String USER_NAME = "admin";

    String PASSWORD = "password";

    String PUBLISH_TOPIC = "tourist_enter";//发布主题

    String RESPONSE_TOPIC = "message_arrived";//响应主题

    /**
     * 跟设备绑定 唯一ID
     */
     String CLIENT_ID = Build.SERIAL;

}
