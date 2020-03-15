package com.mrcodesniper.pushlayer_module;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MsgReceiveCallback implements MqttCallback {

    public static final String TAG="MsgReceiveCallback";

    private Context context;
    private ConnectCallback connectCallback;
    private Messenger messenger;
    private Gson mGson;

    public MsgReceiveCallback(Context context, ConnectCallback connectCallback, Messenger messenger) {
        this.context = context;
        this.connectCallback = connectCallback;
        this.messenger = messenger;
        mGson=new Gson();
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.i(TAG, "连接断开 ");
        PushManager.getInstance().doClientConnection(context,connectCallback);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String json = new String(message.getPayload());
        PushBean pushBean=mGson.fromJson(json,PushBean.class);
        Object obj=pushBean.getContent();
        String contentJson=mGson.toJson(obj);
        //收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putString("data", contentJson);
        msg.setData(data);
      //  msg.replyTo = mGetReplyMessenger;
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        PushManager.getInstance().response("message arrived");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
