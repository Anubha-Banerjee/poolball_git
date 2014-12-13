package anubha.multiplayer.poolball;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class GameLoop {
 GameLoop(final boolean isServer, final String ip) {
	 
	 	
		new Thread(new Runnable() {
	        public void run() { 
	        	Socket sock = null;  
	    		String otherPhoneMessage = null;    
	    		boolean again = true;
	    		long lastTime = 0;
	    		long currentTime = 0;
	    		boolean ballInMyCourt = false;
	    		PrintWriter outp = null;
	    		BufferedReader inp = null;
	    		double y_start = Draw.wallWidth + Draw.ballRadius, y_end = MainActivity.screenHeight - (Draw.wallWidth + Draw.topToolbarHeight + Draw.ballRadius);
	    		
	    				
	    		try {
	    			
	    			if(!isServer) {
	    				ballInMyCourt = false;
		      			Draw.player = "RIGHT";
		      			Draw.y = -100;
		      			sock = new Socket(ip, 8888);	      		
	    			}
	    			else {
	    				ballInMyCourt = true;
		    			Draw.player = "LEFT";
		    			Draw.y = 100;
	    				ServerSocket sockServer = new ServerSocket(8888);
		    			//sock = sockServer.accept();		    		
	    			}
	    			  //outp = new PrintWriter(sock.getOutputStream(), true);
			    	  //inp = new BufferedReader(new InputStreamReader(sock.getInputStream()));		
		        	  while(again) {		        		  
		        		if(ballInMyCourt) {
		        			// first of all get the current time
							currentTime = System.nanoTime();
							double timeDiff = (double) (currentTime - lastTime) / 1000000000.0;
							boolean ballGoneOutOfScreen =  Draw.update(MainActivity.sensorAx, MainActivity.sensorAy, MainActivity.sensorAz, timeDiff);
							if(ballGoneOutOfScreen) {
								Log.e("xolo", "Giving ball");
								ballInMyCourt = false;
								outp.println("TakeBall");
								outp.println(Draw.vx);								
				      			outp.println(Draw.vy);
				      			
				      			//outp.println(Draw.x);
				      			Log.e("xolo the start", String.valueOf(Draw.y));
				      			outp.println((Draw.y - y_start) / (y_end - y_start));			      			
							}
							lastTime = currentTime;
		        		}
		        		
		        		
		        		else {		        			
		        			otherPhoneMessage = inp.readLine();		        			
		        			if(otherPhoneMessage.equals("TakeBall")) {
		        				Log.e("xolo", "taking ball");
		        				Draw.vx = Double.parseDouble(inp.readLine());
		        				Draw.vy = Double.parseDouble(inp.readLine());
		        				
		        				if(Draw.player.equals("RIGHT"))
		        					Draw.x = 0;
		        				else
		        					Draw.x = MainActivity.screenWidth-Draw.ballRadius;
		        				
		        				Draw.y = (Double.parseDouble(inp.readLine()) * (y_end - y_start)) + y_start ;
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
}
