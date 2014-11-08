/*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* Please send inquiries to huber AT ut DOT ee
*/

package com.in.mobile.gesture.ad;  

 
import java.io.IOException; 
 
import com.in.mobile.common.utilities.Commons;
import com.in.mobile.database.adcontainer.DatabaseCommons;
import com.google.android.gcm.GCMRegistrar;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;

/*
 * author Huber Flores
 * in-mobile, 2014
 */

public class AdMechanism extends ActionBarActivity {
	
	private static final String TAG = "Touch";
	
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

        new AdContentLoader(frameLayout, size, this);
         
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
  	
  	    //extract to sdcard
  	    //extractDatabaseFile(new DatabaseCommons());
  	    
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
	
	
	 public void extractDatabaseFile(DatabaseCommons db){
		 try {
			 db.copyDatabaseFile();
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
	  }
	  
    
    
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
