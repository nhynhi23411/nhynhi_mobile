package com.nhynhi.k23411tapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {

    public interface OnSmsReceivedListener {
        void onSmsReceived(String phone, Date time, String content);
    }

    private OnSmsReceivedListener listener;

    public void setOnSmsReceivedListener(OnSmsReceivedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] arrMessages = (Object[]) bundle.get("pdus");
        if (arrMessages == null) return;

        String format = bundle.getString("format");
        String phone, content;
        Date time;
        byte[] bytes;

        for (int i = 0; i < arrMessages.length; i++) {
            bytes = (byte[]) arrMessages[i];
            SmsMessage message = SmsMessage.createFromPdu(bytes, format);

            phone = message.getDisplayOriginatingAddress();
            time = new Date(message.getTimestampMillis());
            content = message.getMessageBody();

            if (listener != null) {
                listener.onSmsReceived(phone, time, content);
            }
        }
    }
}
