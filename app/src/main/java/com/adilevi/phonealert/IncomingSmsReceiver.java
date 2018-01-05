package com.adilevi.phonealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;

public class IncomingSmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus == null) {
                        Log.e("IncomingSmsReceiver ", "no pdus");
                        return;
                    }

                    ContactsPref pref = new ContactsPref(context);
                    ArrayList<String> contactNames = new ArrayList<>(pref.getContactNames());
                    ArrayList<String> contactPhones = new ArrayList<>(pref.getContactPhones());

                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        String msg_from = msgs[i].getOriginatingAddress();
                        String phone = msg_from.substring(msg_from.length() > 9 ? msg_from.length() - 9 : 0);
                        if (contactPhones.contains(phone)) {
                            int indexOf = contactPhones.indexOf(phone);
                            if (indexOf != -1) {
                                String name = contactNames.get(indexOf);
                                AlertActivity.startActivity(context, name, msgs[i].getDisplayMessageBody());
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.e("IncomingSmsReceiver ", e.getMessage());
                }
            }
        }
    }
}