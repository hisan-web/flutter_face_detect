import 'dart:async';

import 'package:face/model/active_file_info_model.dart';
import 'package:face/model/version_info_model.dart';
import 'package:flutter/services.dart';

import 'enum/face_detect_orient_priority_enum.dart';

class FaceDetectPlugin {
  static const MethodChannel _methodChannel = const MethodChannel('com.hs.face/method');

  /// 引擎sdk注册
  static Future<bool> activeOnLine(String appId, String sdkKey) async {
    final bool result = await _methodChannel.invokeMethod('activeOnLine',{'appId': appId, 'sdkKey': sdkKey});
    return result;
  }

  /// 获取激活文件
  static Future<ActiveFileInfoModel> getActiveFileInfo() async {
    var result = await _methodChannel.invokeMethod('getActiveFileInfo');
    ActiveFileInfoModel activeFileInfo = ActiveFileInfoModel.fromJson(result);
    return activeFileInfo;
  }

  /// 获取sdk版本
  static Future<VersionInfoModel> getSdkVersion() async {
    var result = await _methodChannel.invokeMethod('getSdkVersion');
    VersionInfoModel versionInfo = VersionInfoModel.fromJson(result);
    return versionInfo;
  }

  /// 设置视频模式检测角度设置
  static Future<void> setFaceDetectDegree(FaceDetectOrientPriorityEnum faceDetectOrientPriorityEnum) async {
    int faceDetectOrientPriority = 0;
    switch(faceDetectOrientPriorityEnum) {
      case FaceDetectOrientPriorityEnum.ASF_OP_0_ONLY:
        faceDetectOrientPriority = 1;
        break;
      case FaceDetectOrientPriorityEnum.ASF_OP_90_ONLY:
        faceDetectOrientPriority = 2;
        break;
      case FaceDetectOrientPriorityEnum.ASF_OP_270_ONLY:
        faceDetectOrientPriority = 3;
        break;
      case FaceDetectOrientPriorityEnum.ASF_OP_180_ONLY:
        faceDetectOrientPriority = 4;
        break;
      default: break;
    }
    _methodChannel.invokeMethod("setFaceDetectDegree", {"faceDetectOrientPriority": faceDetectOrientPriority});
  }
}
