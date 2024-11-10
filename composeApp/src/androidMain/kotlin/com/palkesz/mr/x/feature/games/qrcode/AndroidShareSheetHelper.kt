package com.palkesz.mr.x.feature.games.qrcode

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class AndroidShareSheetHelper(private val context: Context) : ShareSheetHelper {

    override fun showShareSheet(url: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }
        val shareIntent =
            Intent.createChooser(sendIntent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ContextCompat.startActivity(context, shareIntent, null)
    }

}
