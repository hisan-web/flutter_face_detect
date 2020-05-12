package com.hs.face;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FacePlugin */
public class FacePlugin implements FlutterPlugin, ActivityAware {
  private static final String TAG = "FacePlug";
  private static final String METHOD_CHANNEL = "com.hs.face/method";
  private static final String FACE_DETECT_CAMERA_VIEW_CHANNEL = "com.hs.face/face_detect_camera_view";

  private FlutterPluginBinding flutterPluginBinding;
  private FacePluginMethodCallHandler methodCallHandler;

  /// 1.12版本之前使用此方法进行插件注册
  public static void registerWith(Registrar registrar) {
    final FacePlugin instance = new FacePlugin();
    instance.pluginRegister(registrar.activity(), registrar.messenger());
    registrar.platformViewRegistry().
            registerViewFactory(FACE_DETECT_CAMERA_VIEW_CHANNEL, new FaceDetectCameraFactory(
                    registrar.activity(),
                    registrar.messenger()
            ));
  }

  /// 1.12版本开始使用此方法进行插件注册
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    flutterPluginBinding = binding;
  }

  /// 插件注销
  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    pluginDestroy();
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    pluginRegister(binding.getActivity(), flutterPluginBinding.getBinaryMessenger());
    flutterPluginBinding.getPlatformViewRegistry()
            .registerViewFactory(FACE_DETECT_CAMERA_VIEW_CHANNEL, new FaceDetectCameraFactory(
                    binding.getActivity(),
                    flutterPluginBinding.getBinaryMessenger()
            ));
  }

  @Override
  public void onDetachedFromActivity() {
    pluginDestroy();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  // 插件注册方法
  private void pluginRegister(Activity activity, BinaryMessenger messenger) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      // If the sdk is less than 21 (min sdk for Camera2) we don't register the plugin.
      return;
    }
    methodCallHandler = new FacePluginMethodCallHandler(activity, messenger, METHOD_CHANNEL);
  }

  // 插件销毁
  private void pluginDestroy() {
    flutterPluginBinding = null;
    if (methodCallHandler == null) {
      // Could be on too low of an SDK to have started listening originally.
      return;
    }
    methodCallHandler.onDestroy();
    methodCallHandler = null;
  }
}
