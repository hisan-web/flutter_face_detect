#import "FacePlugin.h"
#if __has_include(<face/face-Swift.h>)
#import <face/face-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "face-Swift.h"
#endif

@implementation FacePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFacePlugin registerWithRegistrar:registrar];
}
@end
