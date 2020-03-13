package com.mrcodesniper.pushlayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PushLayerService.startService(this); //开启服务
    }

    public void btnPushMsg(View v){
        PushLayerService.pushMsg("{\n" +
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
