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

    public static final String TAG="Push";
    /**
     * 开启服务
     */
    public static void startService(Context mContext) {
        mContext.startService(new Intent(mContext, PushLayerService.class));
    }


    private static MqttAndroidClient mqttAndroidClient;

    private MqttConnectOptions mqttConnectOptions;

    public static final String HOST="tcp://192.168.2.104:61613";

    public static final String USER_NAME="admin";

    public static final String PASSWORD ="password";

    public static String PUBLISH_TOPIC  = "tourist_enter";//发布主题
    public static String RESPONSE_TOPIC = "message_arrived";//响应主题

    //MQTT是否连接成功的监听
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            try {
                mqttAndroidClient.subscribe(PUBLISH_TOPIC, 2);//订阅主题，参数：主题、服务质量
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.i(TAG, "连接失败 ");
            doClientConnection();//连接失败，重连（可关闭服务器进行模拟）
        }
    };


    /**
     * 跟设备绑定 唯一ID
     */
    public String CLIENT_ID= Build.SERIAL;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 响应 （收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等）
     *
     * @param message 消息
     */
    public void response(String message) {
        String topic = RESPONSE_TOPIC;
        Integer qos = 2;
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void init(){
        String server=HOST;
        mqttAndroidClient=new MqttAndroidClient(this,server,CLIENT_ID);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i(TAG, "连接断开 ");
                doClientConnection();//连接断开，重连
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i(TAG, "收到消息： " + new String(message.getPayload()));
                //收到消息，这里弹出Toast表示。如果需要更新UI，可以使用广播或者EventBus进行发送
                Toast.makeText(getApplicationContext(), "messageArrived: " + new String(message.getPayload()), Toast.LENGTH_LONG).show();
                //收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等
                response("message arrived");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        mqttConnectOptions=new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setConnectionTimeout(10);
        mqttConnectOptions.setKeepAliveInterval(20);//心跳包间隔
        mqttConnectOptions.setUserName(USER_NAME);
        mqttConnectOptions.setPassword(PASSWORD.toCharArray());
        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + CLIENT_ID + "\"}";
        String topic = PUBLISH_TOPIC;
        Integer qos = 2;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                mqttConnectOptions.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured", e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }
        if (doConnect) {
            doClientConnection();
        }
    }

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        if (!mqttAndroidClient.isConnected() && isConnectIsNomarl()) {
            try {
                mqttAndroidClient.connect(mqttConnectOptions, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
                    doClientConnection();
                }
            }, 3000);
            return false;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 模拟发送消息 通过这个口子发消息
     * @param msg
     */
    public static void pushMsg(String msg){
        String topic = PUBLISH_TOPIC;
        Integer qos = 2;
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, msg.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        try {
            mqttAndroidClient.disconnect(); //断开连接
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
