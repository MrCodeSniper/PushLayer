package com.mrcodesniper.pushlayer;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.mrcodesniper.pushlayer_module.ConnectCallback;
import com.mrcodesniper.pushlayer_module.MessageReceiveListener;
import com.mrcodesniper.pushlayer_module.PushBean;
import com.mrcodesniper.pushlayer_module.PushManager;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void btnConnect(View v) {
        PushManager.getInstance().doClientConnection(this, new ConnectCallback() {
            @Override
            public void onConnectSuccess() {
                Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectFail(Throwable t) {
                Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
            }
        });
        Type fooType = new TypeToken <PushBean<City>>() {}.getType();
        PushManager.getInstance().registerMsgReceiveListener(this, "city",fooType,new MessageReceiveListener<City>() {
            @Override
            public void onReceivedMsg(City data) {
                TextView mTv = findViewById(R.id.textView);
                mTv.setText(data.toString());
            }
        });
        Type weaType = new TypeToken <PushBean<Weather>>() {}.getType();
        PushManager.getInstance().registerMsgReceiveListener(this, "weather", weaType, new MessageReceiveListener<Weather>() {
            @Override
            public void onReceivedMsg(Weather data) {
                TextView mTv = findViewById(R.id.textView);
                mTv.setText(data.toString());
            }
        });
    }


    public void btnPushCityMsg(View v) {
        PushManager.getInstance().mockPushMsg("{\"bizType\":\"city\",\"content\":{\"id\":26,\"pid\":0,\"city_code\":\"101030100\",\"city_name\":\"天津\",\"post_code\":\"300000\",\"area_code\":\"022\",\"ctime\":\"2019-07-11 17:30:08\"}}\n");
    }

    public void btnPushWeatherMsg(View v){
        PushManager.getInstance().mockPushMsg("{\"bizType\":\"weather\",\"content\":{\"day\":\"08日08时\",\"wea\":\"晴\",\"tem\":\"-3℃\",\"win\":\"北风\",\"win_speed\":\"5-6级\"}}\n");
    }
}
