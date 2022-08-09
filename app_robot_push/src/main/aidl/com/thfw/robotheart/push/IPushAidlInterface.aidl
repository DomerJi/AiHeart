// IPushAidlInterface.aidl
package com.thfw.robotheart.push;

import com.thfw.robotheart.push.ICallbackInterface;
// Declare any non-default types here with import statements

interface IPushAidlInterface {

     int onRegisterMsg(int userId,int organId);

     void registerCallback(in ICallbackInterface callback);

     void unregisterCallback(in ICallbackInterface callback);
}