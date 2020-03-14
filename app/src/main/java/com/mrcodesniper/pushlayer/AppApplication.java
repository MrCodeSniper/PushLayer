package com.mrcodesniper.pushlayer;

import android.app.Application;

import com.mrcodesniper.pushlayer_module.PushManager;
import com.mrcodesniper.pushlayer_module.PushOption;
import com.mrcodesniper.pushlayer_module.PushType;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PushManager.getInstance().init(this,
                new PushOption.Builder()
                        .setCleanSession(true)
                        .setPublishChannel(AppPushConfig.PUBLISH_TOPIC)
                        .setResponseChannel(AppPushConfig.RESPONSE_TOPIC)
                        .setPushType(PushType.ONLY_ONCE)
                        .setUserName(AppPushConfig.USER_NAME)
                        .setPassWord(AppPushConfig.PASSWORD)
                        .setServer(AppPushConfig.HOST)
                        .build());
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        PushManager.getInstance().destroy();
    }
}
