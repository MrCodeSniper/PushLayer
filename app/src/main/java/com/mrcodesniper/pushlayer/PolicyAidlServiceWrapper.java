package com.mrcodesniper.pushlayer;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.mrcodesniper.pushlayer_module.IMessageReceiveInterface;
import com.mrcodesniper.pushlayer_module.IMyAidlInterface;

import java.util.List;

public class PolicyAidlServiceWrapper {

    private Context mContext;
    private static PolicyAidlServiceWrapper policyAidlServiceWrapper = null;
    private IMyAidlInterface iRemoteService;

    private PolicyAidlServiceWrapper(Context context) {
        mContext = context;
        // 创建所需绑定的Service的Intent
        Intent intent = new Intent();
        intent.setAction("com.mrcodesniper.pushlayer_module.action.AIDL_SERVICE");
        Intent eintent = new Intent(getExplicitIntent(mContext,intent));
        // 绑定远程Service
        boolean binded = mContext.bindService(eintent, conn, Service.BIND_AUTO_CREATE);
    }
    public static PolicyAidlServiceWrapper getInstance(Context context) {
        if (policyAidlServiceWrapper == null) {
            synchronized (PolicyAidlServiceWrapper.class) {
                if (policyAidlServiceWrapper == null) {
                    policyAidlServiceWrapper = new PolicyAidlServiceWrapper(context);
                }
            }
        }
        return policyAidlServiceWrapper;
    }


    private ServiceConnection conn = new ServiceConnection() {
        // Called when the connection with the service is established
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获取远程Service的onBind方法返回的对象的代理
            iRemoteService = IMyAidlInterface.Stub.asInterface(service);
        }

        // Called when the connection with the service disconnects unexpectedly
        @Override
        public void onServiceDisconnected(ComponentName name) {
                iRemoteService = null;
        }
    };

    //内存扫描的回调
    private IMessageReceiveInterface iMessageReceiveInterface = new IMessageReceiveInterface.Stub(){
        @Override
        public void onReceivedMsg(String pushData) throws RemoteException {
            Log.d("chenhong",pushData);
        }
    };


    public void registerMsg(String bizType){
        if (!isValid()) return;
        try {
            iRemoteService.registerMsgReceiveListener(bizType,iMessageReceiveInterface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void pushMsg(String msg){
        if (!isValid()) return;
        try {
            iRemoteService.basicTypes(0,1L,false,2.0f,2.00,msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断远程服务是否存在
     * @return
     */
    private boolean isValid() {
        return iRemoteService != null;
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

