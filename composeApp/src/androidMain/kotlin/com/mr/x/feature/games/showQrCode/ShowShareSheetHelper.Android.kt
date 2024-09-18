package com.mr.x.feature.games.showQrCode

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

actual class ShowShareSheetHelper(
	private val context: Context
) {

	actual fun showShareSheet(link: String) {
		val sendIntent: Intent = Intent().apply {
			action = Intent.ACTION_SEND
			putExtra(Intent.EXTRA_TEXT, link)
			type = "text/plain"
		}

		val shareIntent =
			Intent.createChooser(sendIntent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		ContextCompat.startActivity(context, shareIntent, null)
	}
}