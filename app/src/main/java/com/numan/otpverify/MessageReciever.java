package com.numan.otpverify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageReciever extends BroadcastReceiver {
    private static MessageListener messageListener;


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data= intent.getExtras();
        Object[] pdus=(Object[]) data.get("pdus");
        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage=SmsMessage.createFromPdu((byte[]) pdus[i]);
            String message=smsMessage.getMessageBody();
            Pattern pattern = Pattern.compile("([0-9]{6})");
            Matcher matcher = pattern.matcher(message);

            if(matcher.lookingAt()){
                messageListener.messageRecieved(matcher.group(1));

            }
        }
    }
    public static void bindListener(MessageListener listener){
        messageListener=listener;
    }
}
