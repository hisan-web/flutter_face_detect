# flutter_face_detect 这是一个Flutter平台的人脸识别插件，可进行人脸检测，人脸跟踪，人脸比对，人脸查找，人脸属性，活体检测，插件可在离线状态运行。
##### 1、人脸检测 - 检测人脸位置、锁定人脸坐标
##### 2、人脸跟踪 - 精确定位并跟踪面部区域位置
##### 3、人脸比对 - 比较两张人脸相似度
##### 4、人脸查找 - 在人脸库中查找相似人脸
##### 5、人脸属性 - 检测人脸性别、年龄等属性
##### 6、活体检测 - 检测是否真人。
----------------------
## 目前打算实现以下几个功能：
#### 1、人脸检测（包含人脸跟踪，属性，活体） - 已完成
#### 2、人脸比对 - 未完成
#### 3、人脸注册 - 未完成
#### 4、人脸管理 - 未完成
----------------------
## 使用方法
<font face="微软雅黑" size="18" color="#ff0000">注意：插件中未进行权限处理，需自行在flutter层面进行权限处理，需要以下权限：</font>
##### 摄像头权限、本地存储权限、网络权限
----------------------
### 1、引擎注册
```
    import 'package:face/face_detect_plugin.dart';

    try {
      bool result = await FaceDetectPlugin.activeOnLine("appid","sdkkey");
      return result;
    } catch (e) {
      print(e.message);
    }
```
### 3、人脸识别，人脸识别view通过原生渲染，使用时需要在view层直接使用FaceDetectCameraView组建
```
    import 'package:flutter/material.dart';
    import 'package:face/face_detect_camera_view.dart';

    class CameraView extends StatefulWidget {
      @override
      _CameraViewState createState() => _CameraViewState();
    }

    class _CameraViewState extends State<CameraView> {

      FaceDetectCameraController faceController;

      @override
      void initState() {
        // TODO: implement initState
        super.initState();
      }

      @override
      Widget build(BuildContext context) {
        return Scaffold(
          backgroundColor: Colors.black,
          body: Container(
            width: double.infinity,
            height: double.infinity,
            child: FaceDetectCameraView(
              showRectView: true,
              faceViewCreatedCallback: (FaceDetectCameraController faceController) {
                this.faceController = faceController
                    ..faceDetectStreamListen((data) {
                      print(data.toString());
                    });
              },
            ),
          ),
        );
      }
    }
```
