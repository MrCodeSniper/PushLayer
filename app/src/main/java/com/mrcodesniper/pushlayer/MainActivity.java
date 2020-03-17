package com.mrcodesniper.pushlayer;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.mrcodesniper.pushlayer.bean.City;
import com.mrcodesniper.pushlayer.bean.Weather;
import com.mrcodesniper.pushlayer_module.AppPushConfig;
import com.mrcodesniper.pushlayer_module.MessageReceiveListener;
import com.mrcodesniper.pushlayer_module.ProcessUtils;
import com.mrcodesniper.pushlayer_module.PushBean;
import com.mrcodesniper.pushlayer_module.PushLayerService;
import com.mrcodesniper.pushlayer_module.PushManager;
import com.mrcodesniper.pushlayer_module.aidl.AidlMessageCallback;
import com.mrcodesniper.pushlayer_module.messenger.MessengerCallback;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {


    private Messenger remoteMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //避免多进程重复绑定
        if (ProcessUtils.isAppProcess(this)) {
            PushLayerService.bindService(this, new MessengerCallback() {
                @Override
                public void onMessengerReturn(Messenger messenger) {
                    remoteMessenger = messenger;
                }
            });
        }
    }

    /**
     * Messenger方式模拟后端推送数据 主进程->Push进程->MMQT服务器->Push进程->主进程
     * @param view
     */
    public void btnMockPush(View view) {
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putString(AppPushConfig.ACTION,"pushMockMsg");
        data.putString(AppPushConfig.PAY_LOAD, "{\"action\":\"weather\",\"payload\":{\"day\":\"08日08时\",\"wea\":\"晴\",\"tem\":\"-3℃\",\"win\":\"北风\",\"win_speed\":\"5-6级\"}}\n");
        msg.setData(data);
        //4.设置客户端client
        msg.replyTo=new Messenger(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg!=null){
                    Bundle bundle=msg.getData();
                    if(bundle!=null){
                        Toast.makeText(MainActivity.this,bundle.getString(AppPushConfig.PAY_LOAD),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        try {
            remoteMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void registerAidlCallback(View view) {
        PushManager.getInstance().registerMsg("city", new AidlMessageCallback() {
            @Override
            public void onMsg(String msg) {

            }
        });
    }



    public void btnRegister(View v) {
//        Type fooType = new TypeToken <PushBean<City>>() {}.getType();
//        PushManager.getInstance().registerMsgReceiveListener(this, "city",fooType,new MessageReceiveListener<City>() {
//            @Override
//            public void onReceivedMsg(City data) {
//                TextView mTv = findViewById(R.id.textView);
//                mTv.setText(data.toString());
//            }
//        });
//        Type weaType = new TypeToken <PushBean<Weather>>() {}.getType();
//        PushManager.getInstance().registerMsgReceiveListener(this, "weather", weaType, new MessageReceiveListener<Weather>() {
//            @Override
//            public void onReceivedMsg(Weather data) {
//                TextView mTv = findViewById(R.id.textView);
//                mTv.setText(data.toString());
//            }
//        });
    }


    public void btnPushCityMsg(View v) {
//        PushManager.getInstance().mockPushMsg
        PolicyAidlServiceWrapper.getInstance(this).pushMsg("{\"bizType\":\"city\",\"content\":{\"id\":26,\"pid\":0,\"city_code\":\"101030100\",\"city_name\":\"天津\",\"post_code\":\"300000\",\"area_code\":\"022\",\"ctime\":\"2019-07-11 17:30:08\"}}\n");
    }

    public void btnPushWeatherMsg(View v) {
     //   PushManager.getInstance().mockPushMsg("{\"bizType\":\"weather\",\"content\":{\"day\":\"08日08时\",\"wea\":\"晴\",\"tem\":\"-3℃\",\"win\":\"北风\",\"win_speed\":\"5-6级\"}}\n");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
