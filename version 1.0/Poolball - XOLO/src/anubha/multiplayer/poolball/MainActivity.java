package anubha.multiplayer.poolball;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity  implements SensorEventListener {
	
	static int screenWidth, screenHeight, screenRotation;
	private SensorManager mSensorManager;
	 
	// acceleration values from accelerometer (in m/s^2)
	public static float sensorAx = 0.0f; 
	public static float sensorAy = 0.0f;   
	public static float sensorAz = 0.0f;
	

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// don't want automatic orientation change 
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.activity_main);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screenWidth = displaymetrics.widthPixels;
		screenHeight = displaymetrics.heightPixels;
	
		 
		//setContentView(R.layout.activity_accel);	        
		setContentView( new Draw(this));	
		
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);  
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		 
		new Thread(new Runnable() {
	        public void run() { 
	        	Socket sock;  
	    		String otherPhoneMessage = null;    
	    		boolean again = true;
	    		long lastTime = 0;
	    		long currentTime = 0;
	    		boolean ballInMyCourt = false;
	    		
	    		try {   
	      			sock = new Socket("192.168.1.2", 8888);		    
	      		    PrintWriter outp = new PrintWriter(sock.getOutputStream(), true);
		    		BufferedReader inp = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		        	  while(again) {		        		  
		        		if(ballInMyCourt) {
		        			Log.e("XOLO", "I have thaaa ball");
							// first of all get the current time
							currentTime = System.nanoTime();
							double timeDiff = (double) (currentTime - lastTime) / 1000000000.0;
							boolean ballGoneOutOfScreen =  Draw.update(sensorAx, sensorAy, sensorAz, timeDiff);
							if(ballGoneOutOfScreen) {
								Log.e("xolo", "Giving ball");
								ballInMyCourt = false;
								outp.println("TakeBall");
								outp.println(Draw.vx);
				      			outp.println(Draw.vy);
				      			outp.println(Draw.x);
				      			outp.println(Draw.y);
				      			Log.e("xolo", "gaaave ball");
							}
							lastTime = currentTime;
		        		}
		        		else {
		        			Log.e("XOLO", "waaaaaiting");
		        			otherPhoneMessage = inp.readLine();		        			
		        			if(otherPhoneMessage.equals("TakeBall")) {
		        				Log.e("xolo", "taking ball");
		        				Draw.vx = Double.parseDouble(inp.readLine());
		        				Draw.vy = Double.parseDouble(inp.readLine() 	);
		        				Draw.x = Double.parseDouble(inp.readLine()) - (screenWidth-50);
		        				Draw.y = Double.parseDouble(inp.readLine());
		        				ballInMyCourt = true;
		        			}
		        		}
					}
	    		}
	    		catch (IOException e) {
	      			// TODO Auto-generated catch block
	      			e.printStackTrace();
	      		}
	        }
	    }).start();				
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	protected void onResume() { 
         super.onResume();      
         mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
     }

     protected void onPause() {
         super.onPause();
         mSensorManager.unregisterListener(this);
     }
     
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		// TODO Auto-generated method stub
		if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) 
		{
			float ax=0, ay=0, az=0;				
			/*
			// HACK: we run our program in landscape mode, so we want the
			// horizontal landscape direction (the longer side of the
			// screen) to be the x - axis
			// but phone's co-ordiate system has smaller side as x-axis and
			// bigger side as y axis.
			// so, we just get x-direction acceleration in ay, and y
			// direction acceleration in ax
			
			ay = sensorEvent.values[0]; 
			ax = sensorEvent.values[1]; 
			*/

			az = sensorEvent.values[2];


			// figure out correct ax, ay depending on orientation
			// croxx fingers and hope this will work on all devices
			// see http://android-developers.blogspot.in/2010/09/one-screen-turn-deserves-another.html for details
			switch(screenRotation)
			{
                case Surface.ROTATION_0:
                    ax = -sensorEvent.values[0];
                    ay = sensorEvent.values[1];
                    break;
                default:
                    ax = sensorEvent.values[1];
                    ay = sensorEvent.values[0];
                    break	;					
			}		
			
			// low pass filter
			final float alpha = 0.5f; 
			sensorAx = alpha * sensorAx + (1 - alpha) * ax;
			sensorAy = alpha * sensorAy + (1 - alpha) * ay;	
			sensorAz = alpha * sensorAz + (1 - alpha) * az;
			    	   
		}	
	}

}
