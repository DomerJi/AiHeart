# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep public class com.thfw.robotheart.activitys.**{*;}
-keep public class com.thfw.robotheart.service.**{*;}
-keep public class com.thfw.robotheart.view.**{*;}
-keep public class com.thfw.robotheart.push.**{*;}
-keep class android.serialport.* {*;}

-keep class android.support.**{*;}
# SVGA
-keep class com.squareup.wire.** { *; }
-keep class com.opensource.svgaplayer.proto.** { *; }

-keep class com.iflytek.** {*;}
-keepattributes Signature

-keepclasseswithmembernames class * {
    native <methods>;
}


-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}

-keep public class com.thfw.mobileheart.R$*{
	public static final int *;
}


#过滤泛型
-keepattributes Signature
#过滤注解
-keepattributes *Annotation*
#过滤js
-keepattributes *JavascriptInterface*

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}


 # 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}


-keep public class * implements java.io.Serializable {
        public *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}





-dontwarn com.amap.api.**
-dontwarn com.a.a.**
-dontwarn com.autonavi.**

-dontwarn org.apache.http.**
-dontwarn com.fasterxml.jackson.databind.**

-dontwarn  com.google.zxing.client.android.**
-dontwarn  com.tencent.weibo.sdk.android.**
-dontwarn  com.bbk.secureunisignon.**
-dontwarn  com.qiniu.android.**
-dontwarn  com.umeng.socialize.**
-dontwarn  net.sf.json.**


-keep class com.amap.api.** {*;}
-keep class com.autonavi.**  {*;}
-keep class com.a.a.**  {*;}

-keep class android.support.** {*;}
-keep class org.** {*;}
-keep class com.fasterxml.jackson.core.** {*;}
-keep public class * extends com.fasterxml.jackson.databind.**
-keep class net.sf.json.** {*;}
-keep class com.qiniu.android.** {*;}
-keep class com.bbk.secureunisignon.** {*;}
-keep class com.tencent.weibo.sdk.android.** {*;}
-keep class com.umeng.socialize.** {*;}
-keep class com.google.zxing.client.android.** {*;}
-keep class com.j256.ormlite.** {*;}
-keep class com.org.simple.eventbus.**{*;}

-keep class com.android.**{*;}



# 下面两条为EventBus
-keepclassmembers class ** {
    public void onEvent*(**);
}



# for squareup picasso
-dontwarn com.squareup.okhttp.**


##友盟推送混淆
-dontwarn com.umeng.message.proguard.**

#-dontwarn com.ut.mini.**
-dontwarn okio.**
-dontwarn com.xiaomi.**
-dontwarn com.squareup.wire.**
-dontwarn android.support.v4.**

-keepattributes *Annotation*

-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }

-keep class okio.** {*;}
-keep class com.umeng.message.protobuffer.* {
    public <fields>;
     public <methods>;
}

-keep class com.umeng.message.* {
    public <fields>;
     public <methods>;
}




