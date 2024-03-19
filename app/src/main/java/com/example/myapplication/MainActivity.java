package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = findViewById(R.id.web);

        webView.loadUrl("https://593a-170-150-253-132.ngrok-free.app"); // Substituir com link da página própria
        webView.getSettings().setUserAgentString("Test");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        class WebAppInterface {
            final Context mContext;

            WebAppInterface(Context c) {
                mContext = c;
            }

            @JavascriptInterface
            public void showToast(String title, String message) {
                String CHANNEL_ID = "10000001";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Nome", importance);
                    channel.setDescription("Descrição");
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

                Notification notification = new NotificationCompat.Builder(this.mContext, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .build();

                NotificationManagerCompat manager = NotificationManagerCompat.from(this.mContext);
                if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1002);
                    }
                }
                manager.notify(1, notification);
            }
        }

        class GeoWebChromeClient extends WebChromeClient {
            private static final int RP_ACCESS_LOCATION = 1001;

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                final String permission = Manifest.permission.ACCESS_FINE_LOCATION;

                if (!(ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, GeoWebChromeClient.RP_ACCESS_LOCATION);
                }
                callback.invoke(origin, true, false);
            }
        }
        webView.setWebChromeClient(new GeoWebChromeClient());
        webView.addJavascriptInterface(new WebAppInterface(this), "AndroidPush");
    }
}
