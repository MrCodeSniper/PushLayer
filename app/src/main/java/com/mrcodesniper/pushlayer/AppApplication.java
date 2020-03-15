package com.mrcodesniper.pushlayer;

import android.app.Application;

import com.mrcodesniper.pushlayer_module.AppPushConfig;
import com.mrcodesniper.pushlayer_module.PushLayerService;
import com.mrcodesniper.pushlayer_module.PushManager;
import com.mrcodesniper.pushlayer_module.PushOption;
import com.mrcodesniper.pushlayer_module.PushType;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PushLayerService.startService(this);
    }

}
