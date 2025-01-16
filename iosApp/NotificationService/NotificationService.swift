import UserNotifications

class NotificationService: UNNotificationServiceExtension {
    var contentHandler: ((UNNotificationContent) -> Void)?
    var bestAttemptContent: UNMutableNotificationContent?

    override func didReceive(_ request: UNNotificationRequest, withContentHandler contentHandler: @escaping (UNNotificationContent) -> Void) {
        self.contentHandler = contentHandler
        bestAttemptContent = (request.content.mutableCopy() as? UNMutableNotificationContent)
        if let userDefaults = UserDefaults(suiteName: "group.com.palkesz.mr.x.shared") {
            var currentNofificationIds = userDefaults.stringArray(forKey: "NotificationIds") ?? [String]()
            currentNofificationIds.append(request.identifier)
            userDefaults.set(currentNofificationIds, forKey: "NotificationIds")
            userDefaults.set(request.content.userInfo, forKey: request.identifier)
            print("Stored notification with id \(request.identifier)")

            let currentBadgeCount = userDefaults.integer(forKey: "BadgeCount")
            UNUserNotificationCenter.current().setBadgeCount(currentBadgeCount + 1)
            userDefaults.set(currentBadgeCount + 1, forKey: "BadgeCount")
        }

        if let bestAttemptContent = bestAttemptContent {
            contentHandler(bestAttemptContent)
        }
    }

    override func serviceExtensionTimeWillExpire() {
        // Called just before the extension will be terminated by the system.
        // Use this as an opportunity to deliver your "best attempt" at modified content, otherwise the original push payload will be used.
        if let contentHandler = contentHandler, let bestAttemptContent = bestAttemptContent {
            contentHandler(bestAttemptContent)
        }
    }
}
