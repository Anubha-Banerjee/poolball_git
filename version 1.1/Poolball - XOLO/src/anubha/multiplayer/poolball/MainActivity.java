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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class MainActivity extends Activity  implements SensorEventListener {
	
	static float screenWidth, screenHeight;
	static int screenRotation;
	private SensorManager mSensorManager;
	 
	// acceleration values from accelerometer (in m/s^2)
	public static float sensorAx = 0.0f; 
	public static float sensorAy = 0.0f;   
	public static float sensorAz = 0.0f;
	public static boolean isServer = false;
	

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
		// don't want automatic orientation change 
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screenWidth = displaymetrics.widthPixels;
		screenHeight = displaymetrics.heightPixels;
	
		 
		//setContentView(R.layout.activity_accel);	        
		//setContentView( new Draw(this));	
		
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);  
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
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
	 /** Called when the user clicks the click me button */
    public void sendMessageClient(View view) {      
    	EditText editText = (EditText) findViewById(R.id.ip_address);    	
    	String ipAddress = editText.getText().toString();
    	isServer = false;
    	setContentView( new Draw(this));    	
    	GameLoop gameLoop = new GameLoop(false, ipAddress);		
    }
    /** Called when the user clicks the click me button */
    public void sendMessageServer(View view) {
    	isServer = true;
    	setContentView( new Draw(this));    	
    	GameLoop gameLoop = new GameLoop(true, "");		
    }
}
