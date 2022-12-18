package com.r.cohen.smartstepstracker.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.r.cohen.smartstepstracker.R
import com.r.cohen.smartstepstracker.logger.Logger
import com.r.cohen.smartstepstracker.repo.StepsTrackerRepo
import com.r.cohen.smartstepstracker.ui.MainActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class TodayStepsWidget : AppWidgetProvider() {
    private val subscriptions = ArrayList<Disposable>()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        subscriptions.filter { !it.isDisposed }.forEach { it.dispose() }
        appWidgetIds.forEach { appWidgetId->
            updateAppWidget(context, appWidgetManager, appWidgetId)
            subscriptions.add(
                StepsTrackerRepo.todayStepsCountChange
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        updateAppWidget(context, appWidgetManager, appWidgetId)
                    }, { e -> Logger.log(e.message) })
            )
        }
    }

    override fun onEnabled(context: Context) {}
    override fun onDisabled(context: Context) {}
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    StepsTrackerRepo.getTodayStepsCount { count ->
        val views = RemoteViews(context.packageName, R.layout.today_steps_widget)
        views.setTextViewText(R.id.textviewCount, String.format("%,d", count))
        val launchMainIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widgetInnerViewsLayout, launchMainIntent)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}