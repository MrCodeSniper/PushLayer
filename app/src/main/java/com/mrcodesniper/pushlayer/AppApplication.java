package com.mrcodesniper.pushlayer;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.mrcodesniper.pushlayer_module.ProcessUtils;
import com.mrcodesniper.pushlayer_module.PushLayerService;
import com.mrcodesniper.pushlayer_module.PushManager;

/**
 * @author ch
 */
public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(ProcessUtils.isAppProcess(this)){
            PushManager.getInstance().init(this,null);
        }
    }

}
