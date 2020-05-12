package com.hs.face;

import android.app.Activity;

import androidx.annotation.NonNull;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class FacePluginMethodCallHandler implements MethodCallHandler {
    private static final String TAG = "MethodCallHandlerImpl";
    private MethodChannel methodChannel;

    private FaceMethodCall faceMethodCall;



    public FacePluginMethodCallHandler(Activity activity, BinaryMessenger messenger, String methodChannelId) {
        //
        methodChannel = new MethodChannel(messenger, methodChannelId);
        methodChannel.setMethodCallHandler(this);
        //
        faceMethodCall = new FaceMethodCall(activity);
    }

    /// 销毁通道
    public void onDestroy() {
        methodChannel.setMethodCallHandler(null);
        methodChannel = null;
    }

    /// 事件分发
    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "activeOnLine": // 引擎注册
                String appId = call.argument("appId");
                String sdkKey = call.argument("sdkKey");
                faceMethodCall.handlerActiveOnline(appId, sdkKey, result);
                break;
            case "getActiveFileInfo": // 获取激活文件
                faceMethodCall.handlerGetActiveFileInfo(result);
                break;
            case "getSdkVersion": // 获取sdk版本
                faceMethodCall.handlerGetSdkVersion(result);
                break;
            case "setFaceDetectDegree": //设置视频模式检测角度
                int faceDetectOrientPriority = (int) call.argument("faceDetectOrientPriority");
                faceMethodCall.handlerSetFaceDetectOrientPriority(faceDetectOrientPriority, result);
                break;
            default:
                result.notImplemented();
        }
    }
}
