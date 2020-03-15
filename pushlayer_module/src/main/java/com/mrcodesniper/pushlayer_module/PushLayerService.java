package com.mrcodesniper.pushlayer_module;

import android.app.Service;
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

/**
 * 单进程service
 *
 * @author codeSniper
 */
public class PushLayerService extends Service {

    public static final String TAG = "PushLayerService";

    private Messenger mMessenger = new Messenger(new MessengerHandler());

    private  class MessengerHandler extends Handler {
        /**
         * @param msg 主进程推过来的消息
         */
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "收到主进程推过来的消息");
            Bundle bundle = msg.getData();
            PushManager.getInstance().setSimpleMsgCallback(PushLayerService.this,msg.replyTo);
            if (bundle != null) {
                String data=bundle.getString("key");
                String action=bundle.getString("action");
                if(TextUtils.equals("pushMockMsg",action)){
                    PushManager.getInstance().mockPushMsg(data);
                }
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
        PushManager.getInstance().setSimpleMsgCallback(this,mMessenger);
        return mMessenger.getBinder();
    }

    /**
     * 绑定服务
     *
     * @param context    环境
     * @param connection 连接
     */
    public static void bindService(Context context, ServiceConnection connection) {
        Intent mIntent = new Intent(context, PushLayerService.class);
        context.bindService(mIntent, connection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand" + ProcessUtils.getProcessName(this));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        PushManager.getInstance().destroy();
        super.onDestroy();
    }
}
