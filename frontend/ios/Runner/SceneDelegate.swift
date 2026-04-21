import Flutter
import UIKit

class SceneDelegate: FlutterSceneDelegate {
    override func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        super.scene(scene, openURLContexts: URLContexts)
        // kakao_flutter_sdk가 FlutterApplicationLifeCycleDelegate(AppDelegate 기반)만
        // 구현하므로, Scene에서 받은 URL을 AppDelegate로 수동 전달해야 함
        for context in URLContexts {
            let _ = (UIApplication.shared.delegate as? FlutterAppDelegate)?.application(
                UIApplication.shared,
                open: context.url,
                options: [:]
            )
        }
    }
}
