package com.mrcodesniper.pushlayer;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mrcodesniper.pushlayer_module.ConnectCallback;
import com.mrcodesniper.pushlayer_module.MessageReceiveListener;
import com.mrcodesniper.pushlayer_module.PushManager;

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

        PushManager.getInstance().registerMsgReceiveListener(this, "Main", new MessageReceiveListener() {
            @Override
            public void onReceivedMsg(String msg) {
                TextView mTv = findViewById(R.id.textView);
                mTv.setText(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void btnPushMsg(View v) {
        PushManager.getInstance().mockPushMsg("{\n" +
                "\n" +
                "    \"resultcode\": \"200\",\n" +
                "\n" +
                "    \"reason\": \"successed!\",\n" +
                "\n" +
                "    \"result\": {\n" +
                "\n" +
                "        \"sk\": {\n" +
                "\n" +
                "            \"temp\": \"24\",\n" +
                "\n" +
                "            \"wind_direction\": \"西南风\",\n" +
                "\n" +
                "            \"wind_strength\": \"2级\",\n" +
                "\n" +
                "            \"humidity\": \"51%\",\n" +
                "\n" +
                "            \"time\": \"10:11\"\n" +
                "\n" +
                "        },\n" +
                "\n" +
                "        \"today\": {\n" +
                "\n" +
                "            \"temperature\": \"16℃~27℃\",\n" +
                "\n" +
                "            \"weather\": \"阴转多云\",\n" +
                "\n" +
                "            \"weather_id\": {\n" +
                "\n" +
                "                \"fa\": \"02\",\n" +
                "\n" +
                "                \"fb\": \"01\"\n" +
                "\n" +
                "            },\n" +
                "\n" +
                "            \"wind\": \"西南风3-4 级\",\n" +
                "\n" +
                "            \"week\": \"星期四\",\n" +
                "\n" +
                "            \"city\": \"滨州\",\n" +
                "\n" +
                "            \"date_y\": \"2015年06月04日\",\n" +
                "\n" +
                "            \"dressing_index\": \"舒适\",\n" +
                "\n" +
                "            \"dressing_advice\": \"建议着长袖T恤、衬衫加单裤等服装。年老体弱者宜着针织长袖衬衫、马甲和长裤。\",\n" +
                "\n" +
                "            \"uv_index\": \"最弱\",\n" +
                "\n" +
                "            \"comfort_index\": \"\",\n" +
                "\n" +
                "            \"wash_index\": \"较适宜\",\n" +
                "\n" +
                "            \"travel_index\": \"\",\n" +
                "\n" +
                "            \"exercise_index\": \"较适宜\",\n" +
                "\n" +
                "            \"drying_index\": \"\"\n" +
                "\n" +
                "        },\n" +
                "\n" +
                "        \"future\": [\n" +
                "\n" +
                "            {\n" +
                "\n" +
                "                \"temperature\": \"16℃~27℃\",\n" +
                "\n" +
                "                \"weather\": \"阴转多云\",\n" +
                "\n" +
                "                \"weather_id\": {\n" +
                "\n" +
                "                    \"fa\": \"02\",\n" +
                "\n" +
                "                    \"fb\": \"01\"\n" +
                "\n" +
                "                },\n" +
                "\n" +
                "                \"wind\": \"西南风3-4 级\",\n" +
                "\n" +
                "                \"week\": \"星期四\",\n" +
                "\n" +
                "                \"date\": \"20150604\"\n" +
                "\n" +
                "            },\n" +
                "\n" +
                "            {\n" +
                "\n" +
                "                \"temperature\": \"20℃~32℃\",\n" +
                "\n" +
                "                \"weather\": \"多云转晴\",\n" +
                "\n" +
                "                \"weather_id\": {\n" +
                "\n" +
                "                    \"fa\": \"01\",\n" +
                "\n" +
                "                    \"fb\": \"00\"\n" +
                "\n" +
                "                },\n" +
                "\n" +
                "                \"wind\": \"西风3-4 级\",\n" +
                "\n" +
                "                \"week\": \"星期五\",\n" +
                "\n" +
                "                \"date\": \"20150605\"\n" +
                "\n" +
                "            }\n" +
                "\n" +
                "        ]\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    \"error_code\": 0\n" +
                "\n" +
                "}");
    }
}
