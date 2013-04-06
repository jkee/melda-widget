package org.tarnavsky.melda;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;
import android.widget.RemoteViews;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author jkee
 */
public class UpdateService extends Service {

    private static final String url = "http://gradus.melda.ru/data.json";
    private Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            HttpClient httpClient = new DefaultHttpClient();

            @Override
            public void run() {
                Pair<String, String> update = downloadUpdate(httpClient);
                Intent widgetUpdateIntent = new Intent(MeldaProvider.intentName);
                widgetUpdateIntent.putExtra("timestamp", update.first);
                widgetUpdateIntent.putExtra("temperature", update.second);
                sendBroadcast(widgetUpdateIntent);
            }
        }, 0, 20000);
        Log.i("melda", "service: started");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        Log.i("melda", "service: stopped");
    }

    private static Pair<String, String> downloadUpdate(HttpClient httpClient) {
        try {
            HttpGet get = new HttpGet(url);
            HttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(json);
                String time = jsonObject.getString("timestamp");
                String temperature = jsonObject.getString("temperature");
                Log.d("melda", "Downloaded update");
                return Pair.create(time, temperature);
            } else {
                Log.w("melda", "Can't execute request: " + response.getStatusLine());
            }
        } catch (Exception e) {
            Log.e("melda", "Error in updating widget: ", e);
        }
        return null;
    }
}
