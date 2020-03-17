package com.mrcodesniper.pushlayer_module;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrcodesniper.pushlayer_module.aidl.AidlMessageCallback;
import com.mrcodesniper.pushlayer_module.messenger.MessengerReceiveCallback;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PushManager implements IConnect {

    public static final String TAG = "PushManager";

    private static PushManager manager = new PushManager();

    private MqttAndroidClient client;

    public MqttAndroidClient getClient() {
        return client;
    }

    private PushOption option;

    private MqttConnectOptions mqttConnectOptions;

    private ConnectCallback mConnectCallback;

    private Gson mGson;

    private IMyAidlInterface iRemoteService;

    public void bindAidlService(Context context, ServiceConnection conn){
        Intent intent = new Intent();
        intent.setAction("com.mrcodesniper.pushlayer_module.action.AIDL_SERVICE");
        Intent eintent = new Intent(getExplicitIntent(context,intent));
        context.bindService(eintent, conn, Service.BIND_AUTO_CREATE);
    }

    private Map<String, MessageReceiveListener> msgReceiveListenerMap = new HashMap<String, MessageReceiveListener>();
    private  Map<String, IMessageReceiveInterface> messageReceiveListenerHashMap = new HashMap<String, IMessageReceiveInterface>();
    private Map<String,Type> typeMap=new HashMap<>();

    private PushManager() {
        mGson = new GsonBuilder().enableComplexMapKeySerialization().create();
    }

    public static PushManager getInstance() {
        return manager;
    }

    public void init(Context context, final PushOption option) {
        bindAidlService(context, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iRemoteService = IMyAidlInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                iRemoteService = null;
            }
        });
        if(option==null){
            return;
        }
        this.option = option;
        client = new MqttAndroidClient(context, option.getServer(), Build.SERIAL);
        if (mqttConnectOptions == null) {
            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(option.isCleanSession());
            mqttConnectOptions.setConnectionTimeout((int) option.getConnectTimeout());
            mqttConnectOptions.setKeepAliveInterval(option.getKeepAliveInterval());//心跳包间隔
            mqttConnectOptions.setUserName(option.getUserName());
            mqttConnectOptions.setPassword(option.getPassWord().toCharArray());
        }
    }

    /**
     * Messenger模式数据传递
     * @param context
     * @param messenger
     */
    public void setMsgCallback(Context context, Messenger messenger){
        if(client!=null){
            client.setCallback(new MessengerReceiveCallback(context,mConnectCallback,messenger));
        }
    }

    private void initLastWill(Context context,ConnectCallback listener){
        // last will message
        String message = "{\"terminal_uid\":\"" + Build.SERIAL + "\"}";
        String topic = option.getPublishChannel();
        int qos = option.getPushType().value;
        boolean retained = false;
        if ((!"".equals(message)) || (!"".equals(topic))) {
            // 最后的遗嘱
            try {
                mqttConnectOptions.setWill(topic, message.getBytes(), qos, retained);
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured", e);
                e.printStackTrace();
                Log.i(TAG, "连接失败 ");
                doClientConnection(context, listener);//连接失败，重连（可关闭服务器进行模拟）
                if (listener != null) {
                    listener.onConnectFail(e);
                }
            }
        }
    }


    public void registerMsg(String bizType, AidlMessageCallback callback){
        if (iRemoteService==null) return;
        try {
            iRemoteService.registerMsgReceiveListener(bizType, new IMessageReceiveInterface.Stub(){
                @Override
                public void onReceivedMsg(String pushData) throws RemoteException {
                    Log.d(TAG,"registerMsg:"+pushData);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟服务器推送消息
     *
     * @param msg
     */
    public void mockPushMsg(String msg) {
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
        if (client == null) {
            return;
        }
        initLastWill(context,listener);
        mConnectCallback = listener;
        if (!client.isConnected() && isConnectIsNomarl(context, listener)) {
            try {
                client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
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
                        doClientConnection(context, listener);//连接失败，重连（可关闭服务器进行模拟）
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
                    doClientConnection(context, callback);
                }
            }, 3000);
            return false;
        }
    }

    /**
     * 回收资源
     */
    public void destroy() {
        try {
            if (client != null) {
                client.disconnect(); //断开连接
                client = null;
            }
            if (msgReceiveListenerMap != null) {
                msgReceiveListenerMap.clear();
                msgReceiveListenerMap = null;
            }
            if(typeMap!=null){
                typeMap.clear();
                typeMap=null;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void registerMsgRemote(final Context context, String bizType,IMessageReceiveInterface callback){
        Log.d(TAG,"register......"+ProcessUtils.getProcessName(context));
        messageReceiveListenerHashMap.put("PushManger#" + bizType, callback);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i(TAG, "连接断开 ");
                doClientConnection(context, mConnectCallback);//连接断开，重连
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Log.i(TAG, "收到消息： " + new String(message.getPayload()));
                String json = new String(message.getPayload());
                PushBean pushBean=mGson.fromJson(json,PushBean.class);
                String contentJson=mGson.toJson(pushBean.getContent());
                for (Map.Entry<String, IMessageReceiveInterface> entry : messageReceiveListenerHashMap.entrySet()) {
                    String mBiz = entry.getKey();
                    if (TextUtils.equals(mBiz,"PushManger#"+ pushBean.getBizType())) {
                        IMessageReceiveInterface observer = entry.getValue();
                        if (observer != null) {
                            try {
                                observer.onReceivedMsg(contentJson);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
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
     * 注册消息接收器 根据业务区分开
     *
     * @param listener
     */
    public <T> void  registerMsgReceiveListener(final Context context, String bizType, Type type, MessageReceiveListener<T> listener) {
        msgReceiveListenerMap.put("PushManger#" + bizType, listener);
        typeMap.put(bizType,type);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i(TAG, "连接断开 ");
                doClientConnection(context, mConnectCallback);//连接断开，重连
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Log.i(TAG, "收到消息： " + new String(message.getPayload()));
                String json = new String(message.getPayload());
                PushBean pushBean=mGson.fromJson(json,PushBean.class);
                Type mType=typeMap.get(pushBean.getBizType());
                PushBean data = mGson.fromJson(json, mType);
                for (Map.Entry<String, MessageReceiveListener> entry : msgReceiveListenerMap.entrySet()) {
                    String mBiz = entry.getKey();
                    if (TextUtils.equals(mBiz,"PushManger#"+ data.getBizType())) {
                        MessageReceiveListener observer = entry.getValue();
                        if (observer != null) {
                            observer.onReceivedMsg(data.getContent());
                        }
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
     *
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

    private static Intent getExplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
}
