package com.mrcodesniper.pushlayer_module;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

public class RemoteService extends Service {

    private String TAG = "RemoteService";
    private IRemoteStrviceStub binderStub;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binderStub;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        binderStub = new IRemoteStrviceStub();
    }


    public class IRemoteStrviceStub extends IMyAidlInterface.Stub {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            synchronized (this) {
                PushManager.getInstance().mockPushMsg(aString);
            }
        }

        @Override
        public void registerMsgReceiveListener(String bizType, IMessageReceiveInterface callback) throws RemoteException {
            synchronized (this) {
                if (callback != null) {
                    PushManager.getInstance().registerMsgRemote(RemoteService.this,bizType,callback);
                }
            }
        }
    }

}
