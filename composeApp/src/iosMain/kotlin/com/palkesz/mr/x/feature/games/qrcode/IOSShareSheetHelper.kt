package com.palkesz.mr.x.feature.games.qrcode

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.popoverPresentationController

class IOSShareSheetHelper : ShareSheetHelper {

    override fun showShareSheet(url: String) {
        val viewController = UIActivityViewController(listOf(url), applicationActivities = null)
        val window = UIApplication.sharedApplication.windows.firstOrNull() as UIWindow?
        viewController.popoverPresentationController()?.sourceView = window
        window?.rootViewController?.presentViewController(
            viewController as UIViewController,
            animated = true,
            completion = null,
        )
    }

}
