import 'package:flutter/material.dart';
import 'dart:async';

import 'package:face/face_detect_plugin.dart';
import 'package:face/model/version_info_model.dart';
import 'package:face/model/active_file_info_model.dart';
import 'package:face/enum/face_detect_orient_priority_enum.dart';

import 'face_view.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Home(),
    );
  }
}

class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
  }

  Future<bool> activeOnLine() async {
    try {
      bool result = await FaceDetectPlugin.activeOnLine("CjGWF7wY9uYDeKsUD8xcBdyM2Xkbc2AyjQkpc2gvFpWu","HKyEKmpqQB84uyj7M23bQWi4Uwto7ZLYyoui9SszrbaF");
      return result;
    } catch (e) {
      print(e.message);
    }
  }

  Future<void> getSdkVersion() async {
    try {
      VersionInfoModel result = await FaceDetectPlugin.getSdkVersion();
      print(result.toString());
    } catch (e) {
      print(e.message);
    }
  }

  Future<void> getActiveFileInfo() async {
    try {
      ActiveFileInfoModel result = await FaceDetectPlugin.getActiveFileInfo();
      print(result.toString());
    } catch (e) {
      print(e.message);
    }
  }

  Future<void> setFaceDetectOrientPriority(FaceDetectOrientPriorityEnum faceDetectOrientPriorityEnum) async {
    try {
      await FaceDetectPlugin.setFaceDetectDegree(faceDetectOrientPriorityEnum);
      print(faceDetectOrientPriorityEnum);
      Navigator.pop(context);
    } catch (e) {
      print(e.message);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Plugin example app'),
      ),
      body: Column(
        children: <Widget>[
          Text('Running on: $_platformVersion\n'),
          RaisedButton(
            child: Text("注册引擎"),
            onPressed: activeOnLine,
          ),
          RaisedButton(
            child: Text("获取注册信息"),
            onPressed: getActiveFileInfo,
          ),
          RaisedButton(
            child: Text("获取版本信息"),
            onPressed: getSdkVersion,
          ),
          RaisedButton(
            child: Text("设置视频人脸检测角度"),
            onPressed: () => setFaceDetectOrientPriorityView(context),
          ),
          RaisedButton(
            child: Text("相机模式人脸检测"),
            onPressed: () {
              print("打开相机");
              Navigator.push(context, MaterialPageRoute(builder: (context) => CameraView()));
            },
          )
        ],
      ),
    );
  }

  void setFaceDetectOrientPriorityView(BuildContext context) {
    showModalBottomSheet(context: context, builder: (BuildContext context) {
      return Container(
        height: 240.0,
        color: Color(0xfff1f1f1),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            RaisedButton(
              onPressed: () => setFaceDetectOrientPriority(FaceDetectOrientPriorityEnum.ASF_OP_0_ONLY),
              child: Text("视频模式仅检测0度"),
            ),
            RaisedButton(
              onPressed: () => setFaceDetectOrientPriority(FaceDetectOrientPriorityEnum.ASF_OP_90_ONLY),
              child: Text("视频模式仅检测90度"),
            ),
            RaisedButton(
              onPressed: () => setFaceDetectOrientPriority(FaceDetectOrientPriorityEnum.ASF_OP_180_ONLY),
              child: Text("视频模式仅检测180度"),
            ),
            RaisedButton(
              onPressed: () => setFaceDetectOrientPriority(FaceDetectOrientPriorityEnum.ASF_OP_270_ONLY),
              child: Text("视频模式仅检测270度"),
            ),
            RaisedButton(
              onPressed: () => setFaceDetectOrientPriority(FaceDetectOrientPriorityEnum.ASF_OP_ALL_OUT),
              child: Text("视频模式全方向人脸检测"),
            )
          ]
        ),
      );
    });
  }
}

