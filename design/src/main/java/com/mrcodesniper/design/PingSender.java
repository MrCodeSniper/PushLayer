package com.mrcodesniper.design;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 保持与服务端的心跳连接
 */
public class PingSender implements IPing {

    public static final String TAG= PingSender.class.getSimpleName();

    //定时器
    private ScheduledExecutorService service;

    //心跳间隔外界提供
    private long timeInterval;
    private ScheduledFuture<?> schedule;


    static class PingSendTask implements Runnable{
        @Override
        public void run() {
                //每次心跳生成新的token保存起来  token记录对应一个数据包pingCommand   插入vector中最后一个 等待调度
        }
    }

    @Override
    public void init(PingOption option) {
        //初始化加载配置
        timeInterval=option.timeInterval;
    }

    @Override
    public void start() {
        //开启定时任务
        //一个核心线程来保证心跳执行 尽量在使用时初始化
        if(service==null){
            service=new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = Executors.defaultThreadFactory().newThread(r);
                    t.setName(TAG);
                    return t;
                }
            });
        }
        schedule = service.schedule(new PingSendTask(), timeInterval, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        if(schedule!=null){
            //不允许线程运行时中断
            schedule.cancel(false);
        }
    }

    @Override
    public void schedule(long timeMills) {
        service.schedule(new PingSendTask(), timeInterval, TimeUnit.SECONDS);
    }
}
