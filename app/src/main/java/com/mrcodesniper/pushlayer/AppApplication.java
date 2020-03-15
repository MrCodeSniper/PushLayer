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

public class AppApplication extends Application {

    public static final String TAG="AppApplication";

    private Messenger mGetReplyMessenger = new Messenger(new MessageHandler());

    public static Messenger mService;

    /**
     * 为了收到Service的回复，客户端需要创建一个接收消息的Messenger和Handler
     */
    private static class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG,"主进程收到推送进程消息");
            //消息处理
            super.handleMessage(msg);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //根据得到的IBinder对象创建Messenger
            mService = new Messenger(service);
            //通过得到的mService 可以进行通信
            Message msg = Message.obtain();
            Bundle data = new Bundle();
            data.putString("key", "Hello! This is client.");
            msg.setData(data);
            msg.replyTo = mGetReplyMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        if(ProcessUtils.isAppProcess(this)){
            PushLayerService.bindService(this,mServiceConnection);
        }
    }

    @Override
    public void onTerminate() {
        unbindService(mServiceConnection);
        super.onTerminate();
    }
}
