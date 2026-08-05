package com.example.estoq.notification.manager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.estoq.MainActivity

object PendingIntentFactory {

    private const val BASE_URI = "estoq://"

    fun itemUpdate(context: Context, itemId: Long, requestCode: Int): PendingIntent {
        return deepLink(context, "${BASE_URI}item/update/$itemId", requestCode)
    }

    fun saleDetail(context: Context, saleId: Long, requestCode: Int): PendingIntent {
        return deepLink(context, "${BASE_URI}sale/detail/$saleId", requestCode)
    }

    fun itemIndex(context: Context): PendingIntent {
        return deepLink(context, "${BASE_URI}item/index", 999001)
    }

    fun storageIndex(context: Context): PendingIntent {
        return deepLink(context, "${BASE_URI}storage/index", 999002)
    }

    fun home(context: Context): PendingIntent {
        return deepLink(context, "${BASE_URI}home", 999003)
    }

    private fun deepLink(context: Context, uri: String, requestCode: Int): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri), context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context, requestCode, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
