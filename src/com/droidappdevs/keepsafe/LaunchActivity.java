package com.droidappdevs.keepsafe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.CallLog;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;

public class LaunchActivity extends Service  {
   
	
	private LocationManager locationManager;
	protected LocationListener listener;
	
	MediaRecorder recorder;
	//int bv = Build.VERSION.SDK_INT;

	
	class GPSAsync implements LocationListener
	{
		private Location currentBestLocation = null;
		
		
		
		protected void startInnerClass() 
		{
			System.out.println("---------------------SYNC TASK-----------------");
			// TODO Auto-generated method stub
			locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		    final boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		    if(!statusOfGPS)
		         turnGPSOn();
		    
		    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		    System.out.println("---------------------LOCATION LISTENER CALLED-----------------");
		    
		    
		    
		    
		    final Handler hand = new Handler();
			hand.postDelayed(new Runnable() {
					@Override
					public void run()
					{
						 	Location lastKnownNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						    Location lastKnownGPSLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						    if(isBetterLocation(lastKnownNetworkLocation,currentBestLocation))
						     currentBestLocation=lastKnownNetworkLocation; 
						    if(lastKnownGPSLocation!=null)
						    if(isBetterLocation(lastKnownGPSLocation,currentBestLocation))
							     currentBestLocation=lastKnownGPSLocation;	    
						    System.out.println("---------------------LOCATION DETERMINED-----------------");
						    if(currentBestLocation!=null)
						    {
							double latitude=currentBestLocation.getLatitude();
							double longitude=currentBestLocation.getLongitude();
							String Text = "My current location is:\n" +"Latitude =" +latitude+"\nLongitude ="+ longitude;
							Geocoder geo = new Geocoder(LaunchActivity.this.getApplicationContext(), Locale.getDefault());
							List<Address> addresses;
							try {
								addresses = geo.getFromLocation(currentBestLocation.getLatitude(),currentBestLocation.getLongitude(), 1);
								if (addresses.size() > 0) {       
								      String message ="\n\nAddress:\n"+addresses.get(0).getFeatureName() + 
								        		"," + addresses.get(0).getLocality() +
								        		"," + addresses.get(0).getAdminArea() + 
								        		"," + addresses.get(0).getCountryName();								      
								      	File file = new File(Environment.getExternalStorageDirectory().getPath()+ "/KeepSafe/GPS.txt");
										if(!file.exists())
									    {
									    	file.createNewFile();
									    }
										FileWriter out = new FileWriter(file);
										out.write(Text);
										out.write(message);
										out.close();					
										//locationManager.removeUpdates(gpsLocationListener);
							           // locationManager.removeUpdates(networkLocationListener);
							           // locationManager = null;
								} 
								}catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								}
						    }
						    else
						    {
						    	try{
						    	File file = new File(Environment.getExternalStorageDirectory().getPath()+ "/KeepSafe/GPS.txt");
								if(!file.exists())
							    {
							    	file.createNewFile();
							    }
								FileWriter out = new FileWriter(file);
								out.write("Location is unavailable");
								out.close();
						    	} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						    	
						    }
						    locationManager.removeUpdates(listener);
							  
						    if(!statusOfGPS)
						         turnGPSOff();
						    System.out.println("--------------------END OF ASYNC TASK-----------------");
					}
			},15000);
		    
			

		}

		private void turnGPSOn(){   

		    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);   
		    if(!provider.contains("gps")){      
		        final Intent poke = new Intent();  
		        poke.setClassName("com.android.settings","com.android.settings.widget.SettingsAppWidgetProvider");           poke.addCategory(Intent.CATEGORY_ALTERNATIVE);   
		        poke.setData(Uri.parse("3"));      
		        sendBroadcast(poke);  
		   }  }    

		public void turnGPSOff()
		{
		  String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		  if(provider.contains("gps")){ //if gps is enabled
		      final Intent poke = new Intent();
		      poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		      poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
		      poke.setData(Uri.parse("3")); 
		      sendBroadcast(poke);
		  }
		}
		
		 
			@Override
			public void onLocationChanged(Location location) 
			{
				Location lastKnownNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				Location lastKnownGPSLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if(lastKnownNetworkLocation!=null)
				if(isBetterLocation(lastKnownNetworkLocation,currentBestLocation))
				     currentBestLocation=location;
				if(lastKnownGPSLocation!=null)
				if(isBetterLocation(lastKnownGPSLocation, currentBestLocation))
					currentBestLocation=location;
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}

		
		
		protected boolean isBetterLocation(Location location, Location currentBestLocation) 
		{
			final int ONE_MINUTE = 1000 * 60 * 1;
			    if (currentBestLocation == null)
			        // A new location is always better than no location
			        return true;
			    else
			    {
			    	 // Computing accurate gps location
				      long timeDelta = location.getTime() - currentBestLocation.getTime();
				      boolean isSignificantlyNewer = timeDelta > ONE_MINUTE;
				      boolean isSignificantlyOlder = timeDelta < -ONE_MINUTE;
				      boolean isNewer = timeDelta > 0;
				     // Check whether the new location fix is more or less accurate
				      int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
				      boolean isLessAccurate = accuracyDelta > 0;
				      boolean isMoreAccurate = accuracyDelta < 0;
				      boolean isSignificantlyLessAccurate = accuracyDelta > 150;
				      
				      if (isMoreAccurate)
				          return true;
				      else if (isNewer && !isSignificantlyLessAccurate)
				          return true;
				      return false;
				  }
		    }

		
		}
	
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		
		final AudioManager am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		turnOnDataConnection(true,LaunchActivity.this);	
		
		
				try {
					startRecording();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	 		   
		 	
				System.out.println("--------------------RECORDING COMPLETED-----------------");

		    final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								 try {
									getCallDetails();
									System.out.println("--------------------CALL+MESSAGE LOG OBTAINED-----------------");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
					},15000);
					
					 

					final Handler handler4 = new Handler();
								handler4.postDelayed(new Runnable() {
											@Override
											public void run() {
												 try {
													 listener=new GPSAsync();
													 ((GPSAsync) listener).startInnerClass();
													System.out.println("--------------------CALL+MESSAGE LOG OBTAINED-----------------");
												} catch (Exception e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}
									},25000);
					
	
			final Handler handler1 = new Handler();
			 			handler1.postDelayed(new Runnable() {
			 				@Override
			 				public void run() {			 					
			 					final String files[]=new String[4];
			 					files[0]=Environment.getExternalStorageDirectory().getPath()+ "/KeepSafe/AUDIO.mp4";
			 					files[1]=Environment.getExternalStorageDirectory().getPath()+ "/KeepSafe/GPS.txt";
			 					files[2]=Environment.getExternalStorageDirectory().getPath()+ "/KeepSafe/CallLogs.txt";
			 					files[3]=Environment.getExternalStorageDirectory().getPath()+ "/KeepSafe/SMS.txt";
			 					Compress comp = new Compress(files,Environment.getExternalStorageDirectory().getPath()+"/KeepSafe/KeepSafe.zip");
			 					comp.zip();
			 					
			 					System.out.println("--------------------FILES ZIPPED AND DATA TURNED ON-----------------");
			 				}
			 			}, 45000);		
			 			
			 			 
			 			
		 	final Handler handler2 = new Handler();
			 		handler2.postDelayed(new Runnable() {
			 			
			 				@Override
			 				public void run() { try {   
			 							
			 							StrictMode.ThreadPolicy policy = new
			                            StrictMode.ThreadPolicy.Builder().permitAll().build();
			                            StrictMode.setThreadPolicy(policy);
			                            BufferedReader br = new BufferedReader(new FileReader(new File(Environment.getExternalStorageDirectory(), "/androidsettings/mail.txt")));
			                            String mailers = br.readLine();
			                            GmailSender sender = new GmailSender("droidappsdevs@gmail.com", "keepsafe");
			                            sender.sendMail("KeepSafe App Team",   
			                                    "This is an automatically generated mail... ",   
			                                    "droidappsdevs@gmail.com",   
			                                   mailers);   //,svarun94@gmail.com,saienthan@gmail.com*/
			                            br.close();
			                            System.out.println("--------------------MAIL COMPILED AND SENT-----------------");
			 				} catch (Exception e) {   
			                            Log.e("SendMail", e.getMessage(), e);   
			                     } 		 	
			 			}
			}, 50000);
		 		
			 		 
			final Handler handler3 = new Handler();
				handler3.postDelayed(new Runnable() {
						@Override
						public void run() {
							am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
							turnOnDataConnection(false,LaunchActivity.this);
							//deleteDirectory(new File(Environment.getExternalStorageDirectory().getPath()+"/KeepSafe"));
							//turnGPSOff();
							BufferedReader br;
							try {
								String ph="";
								br = new BufferedReader(new FileReader(new File(Environment.getExternalStorageDirectory(), "/androidsettings/sos.txt")));
								while((ph=br.readLine())!=null){
								
								sendSMS(ph,"Help me! I'm in distress! Details have been sent to your email-id. Check immediately. \n Keep Safe App team");
								
								}
								br.close();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
							System.out.println("-------------------DATA TURNED OFF AND NORMAL AUDIO PROFILE RESTORED-----------------");
						}
				},60000);
				
				 
	    return super.onStartCommand(intent, flags, startId);
	}
	
	boolean turnOnDataConnection(boolean ON,Context context)
    {

        try{        	
                final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final Class<?> conmanClass = Class.forName(conman.getClass().getName());
                final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
                iConnectivityManagerField.setAccessible(true);
                final Object iConnectivityManager = iConnectivityManagerField.get(conman);
                final Class<?> iConnectivityManagerClass =  Class.forName(iConnectivityManager.getClass().getName());
                final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
                setMobileDataEnabledMethod.setAccessible(true);
                setMobileDataEnabledMethod.invoke(iConnectivityManager, ON);
           // }
            return true;
        }
        catch(Exception e){
            return false;
       }     

    }
	
	public void startRecording() throws IOException{
	    String fileName = "AUDIO.mp4";
	    recorder = new MediaRecorder();  
	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); 
	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); 
	    File dir = new File(Environment.getExternalStorageDirectory().getPath()+ "/KeepSafe/");		    
	    dir.mkdirs();
	    recorder.setOutputFile(Environment.getExternalStorageDirectory().getPath()+ "/KeepSafe/"+fileName);
	    recorder.prepare(); 
	    recorder.start();
	    final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				stopRecording();					
			}
		}, 10000);
	   }

	    protected void stopRecording() {
	    recorder.stop();
	    recorder.release();
	    }
	    
	    public static boolean deleteDirectory(File path) {
	        if( path.exists() ) {
	          File[] files = path.listFiles();
	          if (files == null) {
	              return true;
	          }
	          for(int i=0; i<files.length; i++) {
	             if(files[i].isDirectory()) {
	               deleteDirectory(files[i]);
	             }
	             else {
	               files[i].delete();
	             }
	          }
	        }
	        return( path.delete() );
	    }
	    
	    
	    private void getCallDetails() throws IOException {
			File f2 = new File(Environment.getExternalStorageDirectory().getPath()+ "/KeepSafe/SMS.txt");
			if(!f2.exists())
				f2.createNewFile();
			FileWriter file1 = new FileWriter(f2);
			List<Sms> lstSms = new ArrayList<Sms>();
		    Sms objSms = new Sms();
		    Uri message = Uri.parse("content://sms/");
		    Cursor c =  getContentResolver().query(message, null, null, null, null);	    
		    if (c.moveToFirst()) {
		    	do{
		            objSms = new Sms();
		           
		            objSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
		            objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));

		            lstSms.add(objSms);
		            String msg = "From: "+ objSms.getAddress() + "\n" + objSms.getMsg() + "\n-----------------\n";
		      
		            file1.write(msg);
		    	}while(c.moveToNext());
		    }
		    else 
		    	file1.write("No SMS in Inbox");	  
		    c.close();
			file1.close();
			StringBuffer sb = new StringBuffer();
			Cursor managedCursor = getContentResolver().query( CallLog.Calls.CONTENT_URI,null, null,null, null);
			int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
			int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER ); 
			int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
			int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
			int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
			sb.append( "Call Details :");
			while ( managedCursor.moveToNext() ) {
			String phName =  managedCursor.getString( name );
			String phNumber = managedCursor.getString( number );
			String callType = managedCursor.getString( type );
			String callDate = managedCursor.getString( date );
			Date callDayTime = new Date(Long.valueOf(callDate));
			String callDuration = managedCursor.getString( duration );
			String dir = null;
			int dircode = Integer.parseInt( callType );
			switch( dircode ) {
			case CallLog.Calls.OUTGOING_TYPE:
			dir = "OUTGOING";
			break;

			case CallLog.Calls.INCOMING_TYPE:
			dir = "INCOMING";
			break;

			case CallLog.Calls.MISSED_TYPE:
			dir = "MISSED";
			break;
			}
			sb.append( "\nName:--- "+ phName+ "\nPhone Number:--- "+phNumber +" \nCall Type:--- "+dir+" \nCall Date:--- "+callDayTime+" \nCall duration in sec :--- "+callDuration );
			sb.append("\n----------------------------------");
			}
			managedCursor.close();
			File dir = new File(Environment.getExternalStorageDirectory().getPath()+ "/KeepSafe/");
			dir.mkdirs();
			File f1 = new File(Environment.getExternalStorageDirectory().getPath()+ "/KeepSafe/CallLogs.txt");
			if(!f1.exists())
				f1.createNewFile();
			FileWriter file = new FileWriter(f1);
			
			file.write(sb.toString());
			
			file.close();
			}
	    
	     	private void sendSMS(String phoneNumber, String message)
	     	{
		       SmsManager sms = SmsManager.getDefault();
		       sms.sendTextMessage(phoneNumber, null, message, null, null);
	     	}
	    
}