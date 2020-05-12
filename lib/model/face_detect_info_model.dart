class FaceDetectInfoModel {
  FaceRectModel rect;
  int sex;
  int age;
  int liveness;
  int color;
  String name;

  FaceDetectInfoModel({
    this.rect,
    this.sex: -1,
    this.age: 0,
    this.liveness: -1,
    this.color: -1
  });

  factory FaceDetectInfoModel.fromJson(Map<String, dynamic> json) => FaceDetectInfoModel(
    rect: json["rect"] == null ? new FaceRectModel() : FaceRectModel.fromJson(json["rect"]),
    sex: json["sex"] == null ? -1 : json["sex"],
    age: json["age"] == null ? 0 : json["age"],
    liveness: json["liveness"] == null ? -1 : json["liveness"],
    color: json["color"] == null ? -1 : json["color"],
  );

  Map<String, dynamic> toJson() => {
    "rect": rect == null ? null : rect,
    "sex": sex == null ? null : sex,
    "age": age == null ? null : age,
    "liveness": liveness == null ? null : liveness,
    "color": color == null ? null : color
  };
}

class FaceRectModel {
  int bottom;
  int left;
  int right;
  int top;

  FaceRectModel({
    this.bottom: 0,
    this.left: 0,
    this.right: 0,
    this.top: 0
  });

  factory FaceRectModel.fromJson(Map<String, dynamic> json) => FaceRectModel(
    bottom: json["bottom"] == null ? 0 : json["bottom"],
    left: json["left"] == null ? 0 : json["left"],
    right: json["right"] == null ? 0 : json["right"],
    top: json["top"] == null ? 0 : json["top"],
  );

  Map<String, dynamic> toJson() => {
    "bottom": bottom == null ? null : bottom,
    "left": left == null ? null : left,
    "right": right == null ? null : right,
    "top": top == null ? null : top
  };
}