import ComposeApp
import FirebaseAppCheck
import FirebaseCore
import FirebaseMessaging
import SwiftUI

class MrXAppCheckProviderFactory: NSObject, AppCheckProviderFactory {
    func createProvider(with app: FirebaseApp) -> AppCheckProvider? {
        return AppAttestProvider(app: app)
    }
}

class AppDelegate: NSObject, UIApplicationDelegate, MessagingDelegate, UNUserNotificationCenterDelegate {
    var notificationHelper: IOSNotificationHelper? = nil
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        NapierHelperKt.doInitNapier()
        KoinHelperKt.doInitKoin()
        if _isDebugAssertConfiguration() {
            AppCheck.setAppCheckProviderFactory(AppCheckDebugProviderFactory())
        } else {
            AppCheck.setAppCheckProviderFactory(MrXAppCheckProviderFactory())
        }
        FirebaseApp.configure()
        application.registerForRemoteNotifications()
        notificationHelper = IOSNotificationHelper()
        Messaging.messaging().delegate = self
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound], completionHandler: { granted, _ in
            if granted {
                UNUserNotificationCenter.current().delegate = self
            }
        })
        return true
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        print("Registered for remote notifications")
        Messaging.messaging().apnsToken = deviceToken
    }
    
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("DidReceiveRegistrationToken: \(fcmToken ?? "null")")
        notificationHelper?.uploadToken(token: fcmToken)
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
        print("Received notification: \(userInfo)")
        
        switch response.actionIdentifier {
        case UNNotificationDismissActionIdentifier:
            print("Dismissed notification")
        case UNNotificationDefaultActionIdentifier:
            print("Opened notification")
            notificationHelper?.onNotificationClicked(data: userInfo)
        default:
            break
        }
        completionHandler()
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        let userInfo = notification.request.content.userInfo
        print("Will present notification:\n\(userInfo)")
        if notificationHelper?.shouldShowNotification(data: userInfo) == false {
            print("Notification discarded: already viewed.")
            notificationHelper?.clearNotificationsWithCurrentFilter()
            return
        }
        print("Stored notification with id \(notification.request.identifier)")
        notificationHelper?.storeNotification(id: notification.request.identifier, data: userInfo)
        completionHandler([.badge, .banner, .list, .sound])
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    let rinku = RinkuIos(deepLinkFilter: nil, deepLinkMapper: nil)
    
    let notificationHelper = IOSNotificationHelper()
    
    var body: some Scene {
        WindowGroup {
            ContentView().onOpenURL { url in
                rinku.onDeepLinkReceived(url: url.absoluteString)
            }.onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
                if let userDefaults = UserDefaults(suiteName: "group.com.palkesz.mr.x.shared") {
                    let nofificationIds = userDefaults.stringArray(forKey: "NotificationIds") ?? [String]()
                    for nofificationId in nofificationIds {
                        if let userInfo = userDefaults.value(forKey: nofificationId) as? [AnyHashable: Any] {
                            if notificationHelper.shouldShowNotification(data: userInfo) {
                                print("Stored notification with id \(nofificationId)")
                                notificationHelper.storeNotification(id: nofificationId, data: userInfo)
                            } else {
                                notificationHelper.clearNotificationsWithCurrentFilter()
                            }
                        } else {
                            print("No notification userInfo found with id \(nofificationId)")
                        }
                        userDefaults.removeObject(forKey: nofificationId)
                    }
                    userDefaults.removeObject(forKey: "NotificationIds")
                }
            }
        }
    }
}
