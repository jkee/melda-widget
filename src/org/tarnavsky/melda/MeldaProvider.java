package org.tarnavsky.melda;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author jkee
 */
public class MeldaProvider extends AppWidgetProvider {

    private final String url = "http://gradus.melda.ru/data.json";

    private HttpClient client = new DefaultHttpClient();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.melda_layout);
        update(views);
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    private void update(RemoteViews views) {
        try {
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(json);
                String time = jsonObject.getString("timestamp");
                String temperature = jsonObject.getString("temperature");
                updateView(time, temperature, views);
                Log.i("melda", "Updated!");
            } else {
                Log.w("melda", "Can't execute request: " + response.getStatusLine());
            }
        } catch (Exception e) {
            Log.e("melda", "Error in updating widget: ", e);
        }
    }

    private void updateView(String time, String temperature, RemoteViews views) {
        views.setTextViewText(R.id.temperature, temperature + "°");
        views.setTextViewText(R.id.update_time, time);
    }
}
