package com.in.mobile.gesture.ad;

import java.io.File;

import com.in.mobile.common.utilities.Commons;
import com.google.android.gcm.GCMRegistrar;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.widget.ImageView.ScaleType;

/*
 * author Huber Flores
 * in-mobile, 2014
 */

public class AdMechanism extends ActionBarActivity implements OnTouchListener {
	
	private static final String TAG = "Touch";
	
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	public static float IMAGEHEIGHT;
	public static float IMAGEWIDTH;
	public static int SCREENWIDTH;
	public static int SCREENHEIGHT;
	public static boolean SHOWFLAG = false;
	float resizeWidth;
	float resizeHeight;
	
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	
	DynamicAdView dynamicAdView;
	ImageView imageView;
	

	private Bitmap myBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_mechanism);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREENWIDTH = size.x;
        SCREENHEIGHT = size.y;
        
        matrix.setTranslate(1f, 1f);
        matrix.setScale(0.1f, 0.1f);
        
        dynamicAdView= new DynamicAdView(this,SCREENWIDTH,SCREENHEIGHT);
        
        
        File dir = Environment.getExternalStorageDirectory();
  
        File myFile = new File(dir, "ad.jpg");
        
        if (myFile.exists()){
        	BitmapFactory.Options bitmapFatoryOptions=new BitmapFactory.Options();
            bitmapFatoryOptions.inPreferredConfig=Bitmap.Config.RGB_565;
            myBitmap = BitmapFactory.decodeFile(myFile.getAbsolutePath(), bitmapFatoryOptions);
            
            dynamicAdView.setImageBitmap(myBitmap);
            
        }else{ 
        	//default
        	dynamicAdView.setImageResource(R.drawable.ad_image);
        }
       
        
        
        dynamicAdView.setScaleType(ScaleType.MATRIX);
        dynamicAdView.setImageMatrix(matrix);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        dynamicAdView.setLayoutParams(params);
        dynamicAdView.setOnTouchListener(this);
        frameLayout.addView(dynamicAdView);
      
        
        GCMRegistrar.checkManifest(this);
         
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(Commons.DISPLAY_MESSAGE_ACTION));
         
      
        Thread thread = new Thread(new Runnable(){
  	        @Override
  	        public void run() {
  	            try { 
  	            	GCMRegistrar.register(getApplicationContext(), Commons.SENDER_ID);;
  	            	
  	            } catch (Exception e) {
  	                e.printStackTrace();
  	            }
  	        }
  	    });
  	 
  	    thread.start();
  	
    }


    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		resizeAd();
	}

    
    public void resizeAd(){
    	 IMAGEHEIGHT = dynamicAdView.getHeight();
	     IMAGEWIDTH = dynamicAdView.getWidth();
    }
    
    
    
    @Override
	public boolean onTouch(View v, MotionEvent rawEvent) {
		// TODO Auto-generated method stub
	    WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);
	      // ...
	    DynamicAdView view = (DynamicAdView) v;


	      // Handle touch events here...
	      switch (event.getAction() & MotionEvent.ACTION_MASK) {
	      case MotionEvent.ACTION_DOWN:
	         savedMatrix.set(matrix);
	         start.set(event.getX(), event.getY());
	         Log.d(TAG, "mode=DRAG");
	         mode = DRAG;
	         break;
	      case MotionEvent.ACTION_POINTER_DOWN:
	         oldDist = spacing(event);
	         Log.d(TAG, "oldDist=" + oldDist);
	         if (oldDist > 10f) {
	            savedMatrix.set(matrix);
	            midPoint(mid, event);
	            mode = ZOOM;
	            Log.d(TAG, "mode=ZOOM");
	         }
	         break;
	      case MotionEvent.ACTION_UP:
	      case MotionEvent.ACTION_POINTER_UP:
	         mode = NONE;
	         Log.d(TAG, "mode=NONE");
	         break;
	      case MotionEvent.ACTION_MOVE:
	         if (mode == DRAG) {
	            // ...
	            matrix.set(savedMatrix);
	            matrix.postTranslate(event.getX() - start.x,
	                  event.getY() - start.y);
	         }
	         else if (mode == ZOOM) {
	            float newDist = spacing(event);
	            Log.d(TAG, "newDist=" + newDist);
	            if (newDist > 10f) {
	               matrix.set(savedMatrix);
	               float scale = newDist / oldDist;
	               matrix.postScale(scale, scale, mid.x, mid.y);
	            }
	         }
	         break;
	      }

	      view.setImageMatrix(matrix);
	      CalculateResizeSize(matrix);
	      return true; // indicate event was handled
	}

    
    private void CalculateResizeSize(Matrix mMatrix){
		float[] values = new float[9];
		mMatrix.getValues(values);
		resizeWidth = values[Matrix.MSCALE_X] * IMAGEWIDTH;
		resizeHeight = values[Matrix.MSCALE_Y] * IMAGEHEIGHT;
		if(resizeWidth > SCREENWIDTH ){
			SHOWFLAG = true;
		}else{
			SHOWFLAG = false;
		}
		Log.e("TAG", "X " +resizeWidth + "Y  "+ resizeHeight);
	}
	
	   /** Determine the space between the first two fingers */
	   private float spacing(WrapMotionEvent event) {
	      // ...
	      float x = event.getX(0) - event.getX(1);
	      float y = event.getY(0) - event.getY(1);
	      return FloatMath.sqrt(x * x + y * y);
	   }

	   /** Calculate the mid point of the first two fingers */
	   private void midPoint(PointF point, WrapMotionEvent event) {
	      // ...
	      float x = event.getX(0) + event.getX(1);
	      float y = event.getY(0) + event.getY(1);
	      point.set(x / 2, y / 2);
	   }

    
	@Override
	protected void onPause() {
	   super.onPause();
       GCMRegistrar.unregister(this);

	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    
	    unregisterReceiver(mHandleMessageReceiver);
	    GCMRegistrar.onDestroy(this);
	 }
	
	
	private final BroadcastReceiver mHandleMessageReceiver =
		        new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        String newMessage = intent.getExtras().getString(Commons.EXTRA_MESSAGE);
		        
		    }
	};
	  
    
    
    /* Support to other Android platforms start */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ad_mechanism, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_ad_mechanism, container, false);
            return rootView;
        }
    }

}//end of the class
