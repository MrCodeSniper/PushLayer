package com.mrcodesniper.pushlayer_module;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.mrcodesniper.pushlayer_module.messenger.MessengerCallback;

/**
 * 单进程service
 *
 * @author codeSniper
 */
public class PushLayerService extends Service {

    public static final String TAG = "PushLayerService";

    //1.提供服务端Messenger
    private Messenger mMessenger=new Messenger(new MessengerHandler(this));

    private  class MessengerHandler extends Handler{

        private Context context;

        public MessengerHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "收到主进程数据库内容变化数据");
            //2.拿到client messenger用来往客户端推消息
            PushManager.getInstance().setMsgCallback(PushLayerService.this,msg.replyTo);
            Bundle bundle=msg.getData();
            if(bundle!=null){
                String data=bundle.getString(AppPushConfig.PAY_LOAD,"");
                PushManager.getInstance().mockPushMsg(data);
            }
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind" + ProcessUtils.getProcessName(this));
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
        PushManager.getInstance().doClientConnection(this, new ConnectCallback() {
            @Override
            public void onConnectSuccess() {
                Log.d(TAG, "连接成功");
            }

            @Override
            public void onConnectFail(Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
        //2.将当前messenger提供给客户端
        return mMessenger.getBinder();
    }

    /**
     * 绑定服务
     *
     * @param context    环境
     */
    public static void bindService(Context context) {
        Intent mIntent = new Intent(context, PushLayerService.class);
        context.bindService(mIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG,"onServiceConnected:"+name+"&&"+service);
                Messenger mService = new Messenger(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG,"onServiceDisconnected:"+name);
            }
        }, Context.BIND_AUTO_CREATE);
    }


    public static void bindService(Context context, final MessengerCallback callback) {
        Intent mIntent = new Intent(context, PushLayerService.class);
        context.bindService(mIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG,"onServiceConnected:"+name+"&&"+service);
                Messenger mService = new Messenger(service);
                if(callback!=null){
                    callback.onMessengerReturn(mService);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG,"onServiceDisconnected:"+name);
            }
        }, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        PushManager.getInstance().destroy();
        super.onDestroy();
    }
}
