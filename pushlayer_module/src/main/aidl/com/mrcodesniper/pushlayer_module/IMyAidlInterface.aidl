// IMyAidlInterface.aidl
package com.mrcodesniper.pushlayer_module;
import com.mrcodesniper.pushlayer_module.IMessageReceiveInterface;
// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void registerMsgReceiveListener(String bizType,IMessageReceiveInterface callback);
}
