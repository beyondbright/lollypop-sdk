# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/beyondbright/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# 并保留源文件名为"Proguard"字符串，而非原始的类名 并保留行号
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

#SDK
-keep class com.bm.android.thermometer.** { *; }
-keep class com.bm.android.thermometer.sdk.LollypopSDK { *; }
-keep interface com.bm.android.thermometer.sdk.LollypopSDK$LollypopCallback {
    *;
}
-ignorewarnings

#Gson
-keep class cn.lollypop.be.model.** { *; }
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

#Retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

#保持 Serializable 不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}