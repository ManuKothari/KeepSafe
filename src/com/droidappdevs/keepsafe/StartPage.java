package com.droidappdevs.keepsafe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class StartPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_page);
		final Button save = (Button) findViewById(R.id.button1);
		final Button cancel = (Button) findViewById(R.id.button2);
		final EditText sms = (EditText) findViewById(R.id.editText2);
		final EditText mail = (EditText) findViewById(R.id.editText1);
		final EditText sos = (EditText) findViewById(R.id.editText3);
		final Button launch = (Button) findViewById(R.id.button3);
		final CheckBox cb = (CheckBox) findViewById(R.id.checkBox1);
		save.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {            	
            		 		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            			    @Override
            			    public void onClick(DialogInterface dialog, int which) {
            			        switch (which){
            			        case DialogInterface.BUTTON_POSITIVE:
            			        	File dir = new File(Environment.getExternalStorageDirectory().getPath()+ "/androidsettings/");
            	         			dir.mkdirs();
            	         			File f1 = new File(Environment.getExternalStorageDirectory().getPath()+ "/androidsettings/mail.txt");
            	         			try{
            	         			if(!f1.exists())
            	         				f1.createNewFile();
            	         			BufferedWriter br1 = new BufferedWriter(new FileWriter(f1));
            	         			br1.write(mail.getText().toString());
            	         			br1.close();
            	         			File f2 = new File(Environment.getExternalStorageDirectory().getPath()+ "/androidsettings/sms.txt");
            	         			if(!f2.exists())
            	         				f2.createNewFile();
            	         			BufferedWriter br = new BufferedWriter(new FileWriter(f2));
            	         			br.write(sms.getText().toString());
            	         			br.close();
            	         			File f3 = new File(Environment.getExternalStorageDirectory().getPath()+ "/androidsettings/sos.txt");
            	         			if(!f3.exists())
            	         				f3.createNewFile();
            	         			BufferedWriter br3 = new BufferedWriter(new FileWriter(f3));
            	         			br3.write(sos.getText().toString());
            	         			br3.close();
            	         			File f4 = new File(Environment.getExternalStorageDirectory().getPath()+ "/androidsettings/remote.txt");
            	         			if(!f4.exists())
            	         				f4.createNewFile();
            	         			BufferedWriter br4 = new BufferedWriter(new FileWriter(f4));
            	         			if(cb.isChecked())
            	         				br4.write("true");
            	         			else
            	         				br4.write("false");
            	         			br4.close();
            	         			}catch(Exception e){}
            	         			Toast.makeText(getApplicationContext(), "Saved Successfully - Thank You" , Toast.LENGTH_LONG).show();
            			            finish();
            	         			break;

            			        case DialogInterface.BUTTON_NEGATIVE:
            			        	Toast.makeText(getApplicationContext(), "Please change settings again and click Save." , Toast.LENGTH_LONG).show();
            			            break;
            			        }
            			    }
            			};

            		AlertDialog.Builder builder = new AlertDialog.Builder(StartPage.this);
            		builder.setMessage("ARE YOU SURE? \nThe app will launch whenever you send a SMS:\n" + sms.getText().toString() + "\nto this phone and it will mail the details to:\n" + mail.getText().toString() ).setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
           
          }
         });
		 cancel.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 // Perform action on click            	 
            	 finish();
             }
         });
		 launch.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 // Perform action on click            	 
            	 Intent bootIntent = new Intent(getApplicationContext(), LaunchActivity.class);
                 bootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 getApplicationContext().startService(bootIntent);
                 getApplicationContext().stopService(bootIntent); 
             }
         });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_page, menu);
		return true;
	}

}
