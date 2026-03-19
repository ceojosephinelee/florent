import Flutter
import UIKit

class SceneDelegate: FlutterSceneDelegate {
    override func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        super.scene(scene, openURLContexts: URLContexts)
        for context in URLContexts {
            let _ = (UIApplication.shared.delegate as? FlutterAppDelegate)?.application(
                UIApplication.shared,
                open: context.url,
                options: [:]
            )
        }
    }
}
