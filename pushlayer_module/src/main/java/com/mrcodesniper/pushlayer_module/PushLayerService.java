package com.mrcodesniper.pushlayer_module;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;



import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PushLayerService extends Service {

    public static final String TAG="PushLayerService";

    /**
     * 开启服务
     */
    public static void startService(Context mContext) {
        mContext.startService(new Intent(mContext, PushLayerService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
                Log.d(TAG,"连接成功");
            }

            @Override
            public void onConnectFail(Throwable t) {
                Log.e(TAG,t.getMessage());
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        PushManager.getInstance().destroy();
        super.onDestroy();
    }
}
