package com.assoft.stockentry;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.content.FileProvider;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@CapacitorPlugin(name = "ApkInstaller")
public class ApkInstallerPlugin extends Plugin {

    private File apkFile() {
        File dir = new File(getContext().getCacheDir(), "apk");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "StockEntry.apk");
    }

    @PluginMethod
    public void canInstall(PluginCall call) {
        boolean ok = Build.VERSION.SDK_INT < 26 || getContext().getPackageManager().canRequestPackageInstalls();
        JSObject r = new JSObject();
        r.put("allowed", ok);
        call.resolve(r);
    }

    @PluginMethod
    public void openSettings(PluginCall call) {
        try {
            Intent i = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:" + getContext().getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(i);
            call.resolve();
        } catch (Exception e) {
            call.reject("Cannot open settings: " + e.getMessage());
        }
    }

    @PluginMethod
    public void download(final PluginCall call) {
        final String link = call.getString("url");
        if (link == null) {
            call.reject("url missing");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection c = null;
                try {
                    c = (HttpURLConnection) new URL(link).openConnection();
                    c.setInstanceFollowRedirects(true);
                    c.setConnectTimeout(20000);
                    c.setReadTimeout(30000);
                    c.connect();
                    int code = c.getResponseCode();
                    if (code != 200) throw new Exception("HTTP " + code);
                    long total = c.getContentLengthLong();
                    File f = apkFile();
                    InputStream in = c.getInputStream();
                    FileOutputStream out = new FileOutputStream(f);
                    byte[] buf = new byte[16384];
                    int n;
                    long done = 0;
                    int last = -1;
                    while ((n = in.read(buf)) > 0) {
                        out.write(buf, 0, n);
                        done += n;
                        if (total > 0) {
                            int p = (int) (done * 100 / total);
                            if (p != last) {
                                last = p;
                                JSObject o = new JSObject();
                                o.put("percent", p);
                                notifyListeners("progress", o);
                            }
                        }
                    }
                    out.close();
                    in.close();
                    JSObject r = new JSObject();
                    r.put("bytes", done);
                    call.resolve(r);
                } catch (Exception e) {
                    call.reject("Download failed: " + e.getMessage());
                } finally {
                    if (c != null) c.disconnect();
                }
            }
        }).start();
    }

    @PluginMethod
    public void install(PluginCall call) {
        try {
            Context ctx = getContext();
            File f = apkFile();
            if (!f.exists()) {
                call.reject("APK not downloaded");
                return;
            }
            Uri uri = FileProvider.getUriForFile(ctx, ctx.getPackageName() + ".fileprovider", f);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(uri, "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            call.resolve();
        } catch (Exception e) {
            call.reject("Install failed: " + e.getMessage());
        }
    }
}
