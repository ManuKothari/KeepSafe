package com.droidappdevs.keepsafe;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


public class ReceiverTrigger extends BroadcastReceiver {
    
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    public static String message="";
    
    public void onReceive(Context context, Intent intent) {
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras(); 
        try {             
            if (bundle != null) {                 
                final Object[] pdusObj = (Object[]) bundle.get("pdus");    
                BufferedReader br = new BufferedReader(new FileReader(new File(Environment.getExternalStorageDirectory(), "/androidsettings/sms.txt")));
                BufferedReader br1 = new BufferedReader(new FileReader(new File(Environment.getExternalStorageDirectory(), "/androidsettings/remote.txt")));
                String smscode= br.readLine();
                String bool = br1.readLine();
                br.close();
                br1.close();
                for (int i = 0; i < pdusObj.length; i++) {                     
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();                     
                    String senderNum = phoneNumber;
                    message = currentMessage.getDisplayMessageBody(); 
                    //Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);                  
                    // Show Alert
                    
                    if(message.equalsIgnoreCase(smscode)&& bool.equals("true")){     
                    	 Intent bootIntent = new Intent(context, LaunchActivity.class);
                         bootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         context.startService(bootIntent);
                         context.stopService(bootIntent); 
                         abortBroadcast();
                    }
                   
                } // end for loop
              } // bundle is null
 
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
             
        }
    }

	
   
}

