class ActiveFileInfoModel {
  String appId;
  String sdkKey;
  String platform;
  String sdkType;
  String sdkVersion;
  String fileVersion;
  String startTime;
  String endTime;

  ActiveFileInfoModel({
    this.appId: "",
    this.sdkKey: "",
    this.platform: "",
    this.sdkType: "",
    this.sdkVersion: "",
    this.fileVersion: "",
    this.startTime: "",
    this.endTime: ""
  });

  factory ActiveFileInfoModel.fromJson(Map<String, dynamic> json) => ActiveFileInfoModel(
      appId: json["appId"] == null ? "" : json["appId"],
      sdkKey: json["sdkKey"] == null ? "" : json["sdkKey"],
      platform: json["platform"] == null ? "" : json["platform"],
      sdkType: json["sdkType"] == null ? "" : json["sdkType"],
      sdkVersion: json["sdkVersion"] == null ? "" : json["sdkVersion"],
      fileVersion: json["fileVersion"] == null ? "" : json["fileVersion"],
      startTime: json["startTime"] == null ? "" : json["startTime"],
      endTime: json["endTime"] == null ? "" : json["endTime"],
  );

  Map<String, dynamic> toJson() => {
    "appId": appId == null ? null : appId,
    "sdkKey": sdkKey == null ? null : sdkKey,
    "platform": platform == null ? null : platform,
    "sdkType": sdkType == null ? null : sdkType,
    "sdkVersion": sdkVersion == null ? null : sdkVersion,
    "fileVersion": fileVersion == null ? null : fileVersion,
    "startTime": startTime == null ? null : startTime,
    "endTime": endTime == null ? null : endTime,
  };
}