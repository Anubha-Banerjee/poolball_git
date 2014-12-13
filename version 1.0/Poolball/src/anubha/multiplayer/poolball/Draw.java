package anubha.multiplayer.poolball;

import android.R.color;
import android.R.style;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

class Draw extends View
{  
	Paint p;
	static double x = 100;
	static double y = 100;
    static int wallWidth = 20;
	static int topToolbarHeight = 40;
	static int ballRadius = 20;
	static String player = "LEFT";
    
	static double vx;
	// speed
	static double vy;
	static double constantFriction = 0.01;
	Rect r;
	// acceleration
	public static final float DistanceScale = 8000;		
	
    
    public Draw(Context context){    	
        super(context);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStrokeWidth(4);          
    }

    public Draw(Context context, AttributeSet attrs) {
        super(context, attrs);
        r = new Rect(10, 0, 10, 200);
        // TODO Auto-generated constructor stub
    }  
    
     @SuppressLint("InlinedApi")
	@Override
    protected void onDraw(Canvas c)  
    {
        super.onDraw(c);   
    
        p.setColor(Color.RED);
        c.drawCircle((float) x, (float) y, ballRadius, p);
        p.setStyle(Style.FILL);  
        p.setColor(Color.DKGRAY);
        c.drawRect(0, 0, MainActivity.screenWidth, wallWidth, p);
        
        if(player == "LEFT")
        	c.drawRect(0, 0, wallWidth, MainActivity.screenHeight, p);
        if(player == "RIGHT")
        	c.drawRect(MainActivity.screenWidth - wallWidth, 0, MainActivity.screenWidth, MainActivity.screenHeight, p);
        
        c.drawRect(0, MainActivity.screenHeight - (wallWidth + topToolbarHeight), MainActivity.screenWidth, MainActivity.screenHeight, p);  
        //c.drawRect(200, 40, 200, 40, p);
       	invalidate(); 
    }

	public static boolean update(float ax, float ay, float az, double timeDiff) {
		// TODO Auto-generated method stub
		

		// v = u + at
		vx = vx + ax * timeDiff * DistanceScale;
		vy = vy + ay * timeDiff * DistanceScale;
		
		
		// account for friction - proportional to square of the velocity in fluid
		// constant friction can be useful if we want the fish to ultimately stop

		double fricX;  
		double fricY;
	
			fricX = (constantFriction + vx * vx) * timeDiff;
			fricY = (constantFriction + vy * vy) * timeDiff;
		
			
		

		if (fricX > Math.abs(vx)) {  
			vx = 0;
		}
		else {
			vx = (Math.abs(vx) - fricX)*Math.signum(vx);
		}

		if (fricY > Math.abs(vy)) {
			vy = 0;
		}
		else {
			vy = (Math.abs(vy) - fricY)*Math.signum(vy);
		}
		
		double previousX = x;
		double previousY = y;
		
	
		double distanceX = vx * timeDiff;
		
		x = x + distanceX;
		

		float distanceY = (float) (vy * timeDiff);
		 y = y + distanceY;

		 
		 
		// dont let the ball go off the limits
		 
		if( player == "LEFT") {			
			
			if( x-ballRadius < wallWidth ) {
				x = previousX;
				vx = -vx;
			}
			if( x-ballRadius > MainActivity.screenWidth) {
				return true;
			}
		}

		if(player == "RIGHT") {			
			if( x-ballRadius > MainActivity.screenWidth) {
				x = previousX;
				vx = -vx;
			}
			if( x+ballRadius*2 < wallWidth) {
				return true;
			}
		}
		
		// for top and bottom wall collsions
		if(y-ballRadius < wallWidth) {
			y = previousY;
			vy = -vy;
		}
		if(y+ballRadius > MainActivity.screenHeight - (wallWidth + topToolbarHeight)) {
			y = previousY;
			vy = -vy;	
		}
		return false;
	}
	
}