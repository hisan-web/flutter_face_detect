package com.hs.face;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.enums.DetectMode;
import com.hs.face.impl.StreamHandlerImpl;
import com.hs.face.model.DrawInfo;
import com.hs.face.util.ConfigUtil;
import com.hs.face.util.DrawHelper;
import com.hs.face.util.camera.CameraHelper;
import com.hs.face.util.camera.CameraListener;
import com.hs.face.util.face.RecognizeColor;
import com.hs.face.widget.FaceRectView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;

public class FaceDetectCameraView implements PlatformView, MethodCallHandler, OnGlobalLayoutListener {
    private static final String TAG = "FaceView";
    private static final String METHOD_CHANNEL_PREFIX = "com.hs.face/face_detect_camera_view_method_";
    private static final String EVENT_CHANNEL_PREFIX = "com.hs.face/face_detect_camera_view_event_";

    private Activity activity;
    private StreamHandlerImpl streamHandlerImpl;


    /**
     * view
     */
    private View displayView;
    private View previewView;
    private FaceRectView faceRectView;

    /**
     * Camera
     */
    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;
    private Camera.Size previewSize;
    private Integer rgbCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private boolean showRectView = true;
    /**
     * face sdk
     */
    private FaceEngine faceEngine;
    private int afCode = -1;
    private int processMask = FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS;

    FaceDetectCameraView(Activity activity, BinaryMessenger binaryMessenger, int viewId, Object args) {
        this.activity = activity;
        //
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            activity.getWindow().setAttributes(attributes);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }

        // 建立通道
        MethodChannel methodChannel = new MethodChannel(binaryMessenger, METHOD_CHANNEL_PREFIX+viewId);
        methodChannel.setMethodCallHandler(this);
        // 建立Stream通道
        streamHandlerImpl = new StreamHandlerImpl(binaryMessenger, EVENT_CHANNEL_PREFIX+viewId);

        // 载入xml view层
        displayView = activity.getLayoutInflater().inflate(R.layout.face_detect_view_activity,null);
        previewView = displayView.findViewById(R.id.preview_view);
        faceRectView =  displayView.findViewById(R.id.face_rect_view);
        // 添加view监听，在布局结束后才做初始化操作
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public View getView() {
        return displayView;
    }

    @Override
    public void dispose() {
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
        unInitEngine();
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        switch (call.method) {
            case "loadCameraView":
                showRectView = (boolean) call.argument("showRectView");
                break;
            case "initEngine":
                initEngine();
                if (afCode!=ErrorInfo.MOK) {
                    result.error(""+afCode,"引擎初始化失败","");
                } else {
                    result.success(true);
                }
                break;
            case  "unInitEngine":
                unInitEngine();
                if (afCode!=ErrorInfo.MOK) {
                    result.error(""+afCode,"引擎销毁失败","");
                } else {
                    result.success(true);
                }
                break;
            default:
                result.notImplemented();
        }
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.i(TAG,"previewView的尺寸，宽度为："+previewView.getMeasuredWidth()+"，高度为："+previewView.getMeasuredHeight());
        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                Log.i(TAG, "onCameraOpened: " + cameraId + "  " + displayOrientation + " " + isMirror);
                previewSize = camera.getParameters().getPreviewSize();
                // 初始化人脸框绘制工具
                drawHelper = new DrawHelper(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
                        , cameraId, isMirror, false, false);
            }

            @Override
            public void onPreview(byte[] nv21, Camera camera) {
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }
                List<FaceInfo> faceInfoList = new ArrayList<>();
                int code = faceEngine.detectFaces(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);
                if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
                    code = faceEngine.process(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList, processMask);
                    if (code != ErrorInfo.MOK) {
                        return;
                    }
                } else {
                    return;
                }

                List<AgeInfo> ageInfoList = new ArrayList<>();
                List<GenderInfo> genderInfoList = new ArrayList<>();
                List<Face3DAngle> face3DAngleList = new ArrayList<>();
                List<LivenessInfo> faceLivenessInfoList = new ArrayList<>();
                int ageCode = faceEngine.getAge(ageInfoList);
                int genderCode = faceEngine.getGender(genderInfoList);
                int face3DAngleCode = faceEngine.getFace3DAngle(face3DAngleList);
                int livenessCode = faceEngine.getLiveness(faceLivenessInfoList);
                // 有其中一个的错误码不为ErrorInfo.MOK，return
                if ((ageCode | genderCode | face3DAngleCode | livenessCode) != ErrorInfo.MOK) {
                    return;
                }

                if (faceRectView != null && drawHelper != null) {
                    List<DrawInfo> drawInfoList = new ArrayList<>();
                    for (int i = 0; i < faceInfoList.size(); i++) {
                        Log.i(TAG,"性别："+genderInfoList.get(i).getGender());
                        Log.i(TAG,"年龄："+ageInfoList.get(i).getAge());
                        drawInfoList.add(new DrawInfo(drawHelper.adjustRect(faceInfoList.get(i).getRect()), genderInfoList.get(i).getGender(), ageInfoList.get(i).getAge(), faceLivenessInfoList.get(i).getLiveness(), RecognizeColor.COLOR_UNKNOWN, null));
                    }
                    streamHandlerImpl.eventSinkSuccess(JSON.toJSONString(drawInfoList));
                    if (showRectView) {
                        drawHelper.draw(faceRectView, drawInfoList);
                    }
                }
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
                Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };
        cameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(), previewView.getMeasuredHeight()))
                .rotation(activity.getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(rgbCameraId != null ? rgbCameraId : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();
        cameraHelper.start();
    }

    /**
     * 初始化引擎
     */
    private void initEngine() {
        faceEngine = new FaceEngine();
        afCode = faceEngine.init(activity, DetectMode.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(activity),
                16, 20, FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS);
        Log.i(TAG, "initEngine:  init: " + afCode);
        if (afCode != ErrorInfo.MOK) {
            Log.e(TAG, "initEngine error，code is :"+afCode);
        }
    }

    /**
     * 销毁引擎
     */
    private void unInitEngine() {
        if (afCode == 0) {
            afCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + afCode);
        }
    }

    @Override
    public void onGlobalLayout() {
        previewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        // 初始化人脸引擎
        initEngine();
        // 初始化摄像头
        initCamera();
    }
}
