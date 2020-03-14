package com.mrcodesniper.pushlayer_module;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;
import java.util.Map;

public class PushManager implements IConnect {

    public static final String TAG = "PushManager";

    private static PushManager manager = new PushManager();

    private MqttAndroidClient client;

    private PushOption option;

    private MqttConnectOptions mqttConnectOptions;

    private ConnectCallback mConnectCallback;

    private Map<String, MessageReceiveListener> msgReceiveListenerMap = new HashMap<String, MessageReceiveListener>();

    private PushManager() {}

    public static PushManager getInstance() {
        return manager;
    }

    public void init(Context context, @NonNull final PushOption option) {
        this.option = option;
        client = new MqttAndroidClient(context, option.getServer(), Build.SERIAL);
        if(mqttConnectOptions==null){
            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(option.isCleanSession());
            mqttConnectOptions.setConnectionTimeout((int) option.getConnectTimeout());
            mqttConnectOptions.setKeepAliveInterval(option.getKeepAliveInterval());//心跳包间隔
            mqttConnectOptions.setUserName(option.getUserName());
            mqttConnectOptions.setPassword(option.getPassWord().toCharArray());
        }
    }


    /**
     * 模拟服务器推送消息
     * @param msg
     */
    public void mockPushMsg(String msg){
        String topic = option.getPublishChannel();
        int qos = option.getPushType().value;
        boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            client.publish(topic, msg.getBytes(), qos, retained);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void doClientConnection(final Context context, final ConnectCallback listener) {
        if(client==null){
            return;
        }
        mConnectCallback=listener;
        if (!client.isConnected() && isConnectIsNomarl(context,listener)) {
            try {
                client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG, "连接成功 ");
                        try {
                            client.subscribe(option.getPublishChannel(), option.getPushType().value);//订阅主题，参数：主题、服务质量
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        if (listener != null) {
                            listener.onConnectSuccess();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        exception.printStackTrace();
                        Log.i(TAG, "连接失败 ");
                        doClientConnection(context,listener);//连接失败，重连（可关闭服务器进行模拟）
                        if (listener != null) {
                            listener.onConnectFail(exception);
                        }
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl(final Context context, final ConnectCallback callback) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "没有可用网络");
            /*没有可用网络的时候，延迟3秒再尝试重连*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doClientConnection(context,callback);
                }
            }, 3000);
            return false;
        }
    }

    /**
     * 回收资源
     */
    public void destroy(){
        try {
            if(client!=null){
                client.disconnect(); //断开连接
                client=null;
            }
            if(msgReceiveListenerMap!=null){
                msgReceiveListenerMap.clear();
                msgReceiveListenerMap=null;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册消息接收器 根据业务区分开
     *
     * @param listener
     */
    public void registerMsgReceiveListener(final Context context, String bizType, MessageReceiveListener listener) {
        if (listener != null) {
            msgReceiveListenerMap.put("PushManger#" + bizType, listener);
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i(TAG, "连接断开 ");
                doClientConnection(context,mConnectCallback);//连接断开，重连
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i(TAG, "收到消息： " + new String(message.getPayload()));
                for(Map.Entry<String, MessageReceiveListener> entry : msgReceiveListenerMap.entrySet()){
                    String mBiz = entry.getKey();
                    MessageReceiveListener observer = entry.getValue();
                    if(observer!=null){
                        observer.onReceivedMsg(new String(message.getPayload()));
                    }
                }
                //收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等
                response("message arrived");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    /**
     * 响应 （收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等）
     * @param message 消息
     */
    public void response(String message) {
        String topic = option.getResponseChannel();
        int qos = option.getPushType().value;
        boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            client.publish(topic, message.getBytes(), qos, retained);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注销消息接收器
     */
    public void unregisterMsgReceiveListener(String bizType) {
        msgReceiveListenerMap.remove("PushManger#" + bizType);
    }
}
