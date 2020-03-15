package com.mrcodesniper.pushlayer.bean;

import java.io.Serializable;

public class City implements Serializable {

    public int id;

    public int pid;

    public String city_code;

    public String city_name;

    public String post_code;

    public String area_code;

    public String ctime;

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", pid=" + pid +
                ", city_code='" + city_code + '\'' +
                ", city_name='" + city_name + '\'' +
                ", post_code='" + post_code + '\'' +
                ", area_code='" + area_code + '\'' +
                ", ctime='" + ctime + '\'' +
                '}';
    }
}
