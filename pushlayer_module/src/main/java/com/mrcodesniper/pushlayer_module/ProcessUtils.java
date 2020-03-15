package com.mrcodesniper.pushlayer_module;

import android.app.ActivityManager;
import android.content.Context;

import java.util.Iterator;

public class ProcessUtils {

    /**
     * 判断该进程是否是app进程
     * @return
     */
    public static boolean isAppProcess(Context context) {
        String processName = getProcessName(context);
        if (processName == null || !processName.equalsIgnoreCase(context.getPackageName())) {
            return false;
        }else {
            return true;
        }
    }




    /**
     * 获取运行该方法的进程的进程名
     * @return 进程名称
     */
    public static String getProcessName(Context context) {
        int processId = android.os.Process.myPid();
        String processName = null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Iterator iterator = manager.getRunningAppProcesses().iterator();
        while (iterator.hasNext()) {
            ActivityManager.RunningAppProcessInfo processInfo = (ActivityManager.RunningAppProcessInfo) (iterator.next());
            try {
                if (processInfo.pid == processId) {
                    processName = processInfo.processName;
                    return processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return processName;
    }


}
