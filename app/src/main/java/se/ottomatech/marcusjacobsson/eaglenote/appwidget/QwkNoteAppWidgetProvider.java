package se.ottomatech.marcusjacobsson.eaglenote.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import se.ottomatech.marcusjacobsson.eaglenote.R;
import se.ottomatech.marcusjacobsson.eaglenote.activities.MainActivity;
import se.ottomatech.marcusjacobsson.eaglenote.util.QwkNoteTimeUtil;

/**
 * Created by Marcus Jacobsson on 2015-01-25.
 */
public class QwkNoteAppWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String timestamp = prefs.getString("appwidget_id_timestamp"+appWidgetId, "Timestamp");
            String note = prefs.getString("appwidget_id_note"+appWidgetId, "Note details");

            QwkNoteTimeUtil timeUtil = new QwkNoteTimeUtil();
            String formatedTimeStamp = timeUtil.makeTime(timestamp);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

            views.setTextViewText(R.id.tv_app_widget_timestamp, formatedTimeStamp);
            views.setTextViewText(R.id.tv_app_widget_note, note);

            Intent showNote = new Intent(context, MainActivity.class);
            showNote.putExtra("widget_sender", appWidgetId);
            showNote.setData(Uri.parse(showNote.toUri(Intent.URI_INTENT_SCHEME)));
            showNote.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent notesPendingIntent = PendingIntent.getActivity(context, appWidgetId, showNote, PendingIntent.FLAG_UPDATE_CURRENT);


            views.setOnClickPendingIntent(R.id.app_widget_layout_id ,notesPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }
}
