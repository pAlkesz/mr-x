package com.palkesz.mr.x.feature.games.showQrCode

import platform.UIKit.*

actual class ShowShareSheetHelper {

	actual fun showShareSheet(link: String) {
		val av = UIActivityViewController(listOf(link), null)
		val window = UIApplication.sharedApplication.windows.firstOrNull() as UIWindow?
		av.popoverPresentationController()?.sourceView = window
		window?.rootViewController?.presentViewController(
			av as UIViewController,
			animated = true,
			completion = null)
	}
}