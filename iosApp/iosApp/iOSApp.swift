import SwiftUI
import Firebase
import ComposeApp

@main
struct iOSApp: App {

    init(){
        FirebaseApp.configure()
    }
    
    let rinku = RinkuIos.init(deepLinkFilter: nil, deepLinkMapper: nil)
    
    var body: some Scene {
		WindowGroup {
            ContentView().onOpenURL{(url) in
                rinku.onDeepLinkReceived(url: url.absoluteString)
            }
		}
	}
}
