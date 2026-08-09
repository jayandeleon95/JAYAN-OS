package com.jayan.os;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.content.pm.ResolveInfo;
import android.content.pm.ApplicationInfo;
import android.os.BatteryManager;
import android.content.Context;
import android.provider.Settings;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.hardware.camera2.CameraManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.PermissionRequest;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

public class MainActivity extends Activity {
    private WebView web;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        web = new WebView(this);
        setContentView(web);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);

        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient() {
            @Override public void onPermissionRequest(final PermissionRequest request) {
                runOnUiThread(() -> {
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        request.grant(request.getResources());
                    } else {
                        request.deny();
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 41);
                    }
                });
            }
        });
        web.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");
        web.loadUrl("file:///android_asset/index.html");
    }


    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else {
            // A launcher should remain the Home surface rather than accidentally closing.
            web.evaluateJavascript(
                "(function(){try{closeDrawer();closeRecents();closeNotificationCenter();closeControlHub();closeNativeCenter();closeProfiles();closeLauncherSearch();}catch(e){}})();",
                null
            );
        }
    }

    private String drawableToDataUri(Drawable d) {
        int size = 96;
        Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, size, size);
        d.draw(c);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 80, out);
        return "data:image/png;base64," + Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
    }

    public class AndroidBridge {
        @JavascriptInterface
        public String getInstalledApps() {
            JSONArray arr = new JSONArray();
            try {
                PackageManager pm = getPackageManager();
                Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
                Collections.sort(apps, new Comparator<ResolveInfo>() {
                    public int compare(ResolveInfo a, ResolveInfo b) {
                        return a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString());
                    }
                });
                for (ResolveInfo ri : apps) {
                    JSONObject o = new JSONObject();
                    o.put("label", ri.loadLabel(pm).toString());
                    o.put("packageName", ri.activityInfo.packageName);
                    try { o.put("icon", drawableToDataUri(ri.loadIcon(pm))); } catch (Exception ignored) { o.put("icon", ""); }
                    arr.put(o);
                }
            } catch (Exception ignored) {}
            return arr.toString();
        }

        @JavascriptInterface
        public void openApp(String packageName) {
            try {
                Intent launch = getPackageManager().getLaunchIntentForPackage(packageName);
                if (launch != null) startActivity(launch);
            } catch (Exception ignored) {}
        }

        @JavascriptInterface
        public int getBatteryPercent() {
            try {
                BatteryManager bm = (BatteryManager)getSystemService(Context.BATTERY_SERVICE);
                return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            } catch (Exception e) {
                return -1;
            }
        }




        @JavascriptInterface
        public void haptic(int milliseconds) {
            try {
                Vibrator v=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                if(v!=null && v.hasVibrator()){
                    if(Build.VERSION.SDK_INT>=26)v.vibrate(VibrationEffect.createOneShot(Math.max(1,Math.min(milliseconds,60)),VibrationEffect.DEFAULT_AMPLITUDE));
                    else v.vibrate(Math.max(1,Math.min(milliseconds,60)));
                }
            } catch(Exception ignored){}
        }

        @JavascriptInterface
        public void setBrightness(int value) {
            try {
                final float level = Math.max(1, Math.min(255, value)) / 255.0f;
                runOnUiThread(() -> {
                    android.view.WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.screenBrightness = level;
                    getWindow().setAttributes(lp);
                });
            } catch (Exception ignored) {}
        }

        @JavascriptInterface
        public String getNotifications() {
            return JayanNotificationService.snapshot();
        }

        @JavascriptInterface
        public void openNotificationAccessSettings() {
            try { startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")); }
            catch (Exception ignored) {}
        }
        @JavascriptInterface
        public String getDeviceInfo() {
            return "Device: " + Build.MANUFACTURER + " " + Build.MODEL +
                   "<br>Android: " + Build.VERSION.RELEASE +
                   "<br>SDK: " + Build.VERSION.SDK_INT;
        }

        @JavascriptInterface
        public void systemAction(String action) {
            try {
                Intent i = null;
                if ("settings".equals(action)) {
                    i = new Intent(Settings.ACTION_SETTINGS);
                } else if ("wifi".equals(action)) {
                    i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                } else if ("bluetooth".equals(action)) {
                    i = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                } else if ("wallpaper".equals(action)) {
                    i = new Intent(Intent.ACTION_SET_WALLPAPER);
                } else if ("camera".equals(action)) {
                    i = new Intent("android.media.action.STILL_IMAGE_CAMERA");
                } else if ("flash".equals(action)) {
                    CameraManager cm = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
                    String[] ids = cm.getCameraIdList();
                    if (ids.length > 0) {
                        boolean current = getSharedPreferences("jayan", MODE_PRIVATE).getBoolean("flash", false);
                        cm.setTorchMode(ids[0], !current);
                        getSharedPreferences("jayan", MODE_PRIVATE).edit().putBoolean("flash", !current).apply();
                    }
                    return;
                }
                if (i != null) startActivity(i);
            } catch (Exception ignored) {}
        }
    }

    @Override public void onBackPressed() {
        if (web.canGoBack()) web.goBack(); else moveTaskToBack(true);
    }
}
