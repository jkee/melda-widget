package org.tarnavsky.melda;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author jkee
 */
public class MeldaProvider extends AppWidgetProvider {

    private final String url = "http://gradus.melda.ru/data.json";

    public static String intentName = "org.tarnavsky.melda.UPDATE";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(context, UpdateService.class));
        Log.i("melda", "widget: starting service");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("melda", "update ids: " + Arrays.toString(appWidgetIds));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.melda_layout);
        updateViewInit(views, "...", "...");
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(intentName)) {
            String timestamp = intent.getStringExtra("timestamp");
            String temperature = intent.getStringExtra("temperature");

            RemoteViews views =  new RemoteViews(context.getPackageName(), R.layout.melda_layout);
            updateView(timestamp, temperature, views);

            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, MeldaProvider.class));
            Log.d("melda", "ids: " + Arrays.toString(appWidgetIds));
            manager.updateAppWidget(new ComponentName(context, MeldaProvider.class), views);
            manager.updateAppWidget(appWidgetIds, views);
            Log.d("melda", "widget updated");
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, UpdateService.class));
        Log.i("melda", "widget: stopping service");
    }

    private void updateViewInit(RemoteViews views, String o1, String o2) {
        views.setTextViewText(R.id.temperature, o1);
        views.setTextViewText(R.id.update_time, o2);
    }

    private void updateView(String time, String temperature, RemoteViews views) {
        Log.d("melda", "widget time: " + time);
        views.setTextViewText(R.id.temperature, temperature + "°");
        views.setTextViewText(R.id.update_time, time);
    }
}
