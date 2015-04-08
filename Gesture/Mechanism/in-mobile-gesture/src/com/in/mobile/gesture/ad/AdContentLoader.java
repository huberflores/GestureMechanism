/*
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * Please send inquiries to huber AT ut DOT ee
 *
 * author Huber Flores
 * in-mobile, 2014
 */

package com.in.mobile.gesture.ad;

import java.io.File;
import java.io.IOException;

import com.google.android.gcm.GCMRegistrar;
import com.in.mobile.common.utilities.Commons;
import com.in.mobile.database.adcontainer.DatabaseCommons;
import com.in.mobile.database.adcontainer.DatabaseHandler;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class AdContentLoader implements OnTouchListener {

	public static float IMAGEHEIGHT;
	public static float IMAGEWIDTH;
	public static int SCREENWIDTH;
	public static int SCREENHEIGHT;
	public static boolean SHOWFLAG = false;

	Activity activity;

	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	float newX;
	float newY;
	float resizeWidth;
	float resizeHeight;

	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;

	public static DynamicAdView dynamicAdView;
	ImageView imageView;

	DatabaseHandler dataAds;
	
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(
					Commons.EXTRA_MESSAGE);

		}
	};

	public AdContentLoader(Activity context) {

		activity = context;

		GCMRegistrar.checkManifest(activity);

		Point size = new Point();

		activity.getWindowManager().getDefaultDisplay().getSize(size);

		FrameLayout frameLayout = (FrameLayout) activity.getWindow()
				.getDecorView().findViewById(android.R.id.content);

		SCREENWIDTH = size.x;
		SCREENHEIGHT = size.y;

		matrix.setTranslate(1f, 1f);
		matrix.setScale(0.1f, 0.1f);

		dynamicAdView = new DynamicAdView(context, 200, 200, SCREENWIDTH,
				SCREENHEIGHT, "ad.jpg", Color.rgb(255, 0, 0));

		frameLayout.addView(dynamicAdView);

		dataAds = DatabaseHandler.getInstance();
		dataAds.setContext(context);
		
		activity.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Commons.DISPLAY_MESSAGE_ACTION));

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					GCMRegistrar.register(activity.getApplicationContext(),
							Commons.SENDER_ID);
				} catch (Exception e) {
					Log.e("AdMechanism - onCreate", e.toString());
				}
			}
		});

		thread.start();
	}

	@Override
	public boolean onTouch(View v, MotionEvent rawEvent) {
		WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);

		DynamicAdView view = (DynamicAdView) v;

		IMAGEHEIGHT = dynamicAdView.getHeight();
		IMAGEWIDTH = dynamicAdView.getWidth();

		switch (event.getAction() & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_MOVE:

			break;
		}

		return true;
	}

	public void onPause() {
		GCMRegistrar.unregister(activity);
	}

	public void destroy() {
		activity.unregisterReceiver(mHandleMessageReceiver);

		GCMRegistrar.onDestroy(activity);
	}

	public static void adUpdate(final String fileName) {

		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			public void run() {
				// UI code
				File dir = Environment.getExternalStorageDirectory();
				File myFile = new File(dir, fileName);

				BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
				bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
				Bitmap myBitmap2 = BitmapFactory.decodeFile(
						myFile.getAbsolutePath(), bitmapFatoryOptions);

				dynamicAdView.UpdateImage(myBitmap2);
			}
		});
	}

	// Mising database query
	public void adUpdate(int adIdentifier) {
		final String adFilePath = dataAds.getInstance().getDatabaseManager()
				.getAdFilePath(adIdentifier);

		if (adFilePath != null) {
			adUpdate(adFilePath);
		}
	}
	
	public void extractDatabaseFile(DatabaseCommons db) {
		try {
			db.copyDatabaseFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}