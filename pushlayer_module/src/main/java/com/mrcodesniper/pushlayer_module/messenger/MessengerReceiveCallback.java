package com.mrcodesniper.pushlayer_module.messenger;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.google.gson.Gson;
import com.mrcodesniper.pushlayer_module.AppPushConfig;
import com.mrcodesniper.pushlayer_module.ConnectCallback;
import com.mrcodesniper.pushlayer_module.PushBean;
import com.mrcodesniper.pushlayer_module.PushManager;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Messenger
 *
 * @author ch
 */
public class MessengerReceiveCallback implements MqttCallback {

    public static final String TAG = "MessengerCallback";

    private Context context;
    private ConnectCallback connectCallback;
    private Messenger messenger;
    private Gson mGson;

    public MessengerReceiveCallback(Context context, ConnectCallback connectCallback, Messenger messenger) {
        this.context = context;
        this.connectCallback = connectCallback;
        this.messenger = messenger;
        mGson = new Gson();
    }

    @Override
    public void connectionLost(Throwable cause) {
        if(cause!=null){
            Log.i(TAG, "Messenger通信,连接断开:" + cause.getMessage());
        }
        PushManager.getInstance().doClientConnection(context, connectCallback);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String json = new String(message.getPayload());
        PushBean pushBean = mGson.fromJson(json, PushBean.class);
        String contentJson = mGson.toJson(pushBean.getContent());
        //收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putString(AppPushConfig.PAY_LOAD, contentJson);
        msg.setData(data);
        messenger.send(msg);
        //TODO 不止messenger还要回调回去
        PushManager.getInstance().response("message arrived");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
