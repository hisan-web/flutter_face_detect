import 'dart:convert' as convert;

import 'package:face/model/face_detect_info_model.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

typedef void FaceViewCreatedCallback(FaceDetectCameraController controller);

class FaceDetectCameraView extends StatefulWidget {
  final FaceViewCreatedCallback faceViewCreatedCallback;
  final bool showRectView;

  FaceDetectCameraView({
    Key key,
    @required this.faceViewCreatedCallback,
    this.showRectView: true,
  });

  @override
  _FaceDetectCameraViewState createState() => _FaceDetectCameraViewState();
}

class _FaceDetectCameraViewState extends State<FaceDetectCameraView> {
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: 'com.hs.face/face_detect_camera_view',
        onPlatformViewCreated: onPlatformViewCreated,
        creationParamsCodec: const StandardMessageCodec(),
      );
    } else if (defaultTargetPlatform == TargetPlatform.iOS) {
      return UiKitView(
        viewType: 'com.hs.face/face_detect_camera_view',
        onPlatformViewCreated: onPlatformViewCreated,
        creationParamsCodec: const StandardMessageCodec(),
      );
    }
    return Text("$defaultTargetPlatform not support");
  }

  /// 视图创建回调，目的是创建完成视图后才进行channel初始化
  Future<void> onPlatformViewCreated(id) async {
    if (widget.faceViewCreatedCallback == null) {
      return;
    }
    widget.faceViewCreatedCallback(FaceDetectCameraController.init(id, showRectView: widget.showRectView));
  }
}



class FaceDetectCameraController {
  MethodChannel _methodChannel;
  EventChannel _eventChannel;

  /// 人脸数据流监听
  void faceDetectStreamListen(dynamic success, {dynamic error}) {
    _eventChannel.receiveBroadcastStream().listen((data) {
      List dataList = convert.jsonDecode(data);
      success(List<FaceDetectInfoModel>.from(dataList.map((x) => FaceDetectInfoModel.fromJson(x))));
    }, onError: error);
  }

  FaceDetectCameraController.init(int id, {bool showRectView, bool showInfo}) {
    _methodChannel = MethodChannel("com.hs.face/face_detect_camera_view_method_$id");
    _methodChannel.invokeMethod("loadCameraView", {
      "showRectView": showRectView,
    });
    ///
    _eventChannel = EventChannel("com.hs.face/face_detect_camera_view_event_$id");
  }
}