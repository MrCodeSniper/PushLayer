package com.mrcodesniper.design;

public interface IPing {

    public void init(PingOption option);

    public void start();

    public void stop();

    public void schedule(long timeMills);

}
