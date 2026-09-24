package com.example

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class ZannWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.zann_widget_layout)

        fun createPendingIntent(actionValue: String): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("WIDGET_ACTION", actionValue)
            }
            return PendingIntent.getActivity(
                context,
                actionValue.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        views.setOnClickPendingIntent(R.id.widget_header_pill, createPendingIntent("main"))
        views.setOnClickPendingIntent(R.id.widget_button_mic, createPendingIntent("voice"))
        views.setOnClickPendingIntent(R.id.widget_button_camera, createPendingIntent("camera"))
        views.setOnClickPendingIntent(R.id.widget_button_file, createPendingIntent("file"))
        views.setOnClickPendingIntent(R.id.widget_button_gallery, createPendingIntent("gallery"))
        views.setOnClickPendingIntent(R.id.widget_button_live, createPendingIntent("live"))

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
