package com.zybooks.inventorio;

import android.telephony.SmsManager;
import android.util.Log;

public class SMSManager {

    private static final String TAG = "SEND_MESSAGE";

    public static void SendSMSMessage(String phoneNumber, String message)
    {
        try{
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        }
        catch (Exception e)
        {
            Log.e(TAG, "SendSMSMessage: FAILED TO SEND MESSAGE", e);
        }
    }
}
