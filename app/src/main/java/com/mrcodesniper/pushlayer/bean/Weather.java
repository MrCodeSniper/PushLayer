package com.mrcodesniper.pushlayer.bean;

import java.io.Serializable;

public class Weather implements Serializable {

    public String day;

    public String wea;

    public String tem;

    public String win;

    public String win_speed;


    @Override
    public String toString() {
        return "Weather{" +
                "day='" + day + '\'' +
                ", wea='" + wea + '\'' +
                ", tem='" + tem + '\'' +
                ", win='" + win + '\'' +
                ", win_speed='" + win_speed + '\'' +
                '}';
    }
}
