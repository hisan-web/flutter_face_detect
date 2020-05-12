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

  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
  }
}
