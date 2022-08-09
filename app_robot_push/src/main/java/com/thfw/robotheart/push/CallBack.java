package com.thfw.robotheart.push;

import android.os.Process;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CallBack {

    private CallBack() {
        Log.d("IPushAidlImp", "CallBack create ++++++++++++++");
    }

    public static class Holder {
        private static List<ICallbackInterface> callbackInterfaces = new ArrayList<>();
    }

    public static List<ICallbackInterface> getCallbackInterfaces() {
        Log.d("IPushAidlImp", "Holder.callbackInterfaces = " + Holder.callbackInterfaces);
        Log.d("IPushAidlImp", "Process.myPid() = " + Process.myPid());
        return Holder.callbackInterfaces;
    }
}