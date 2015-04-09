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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;

public class AdContentLoader {

	static Activity activity;

	static DynamicAdView dynamicAdView;

	static DatabaseHandler dbHandler;

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(
					Commons.EXTRA_MESSAGE);
		}
	};

	public AdContentLoader(Activity context) {

		activity = context;

		Point screenSize = new Point();

		activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

		GCMRegistrar.checkManifest(activity);

		dynamicAdView = new DynamicAdView(context, 200, 200, screenSize.x,
				screenSize.y, "ad.jpg", Color.rgb(255, 0, 0));

		((ViewGroup) activity.getWindow().getDecorView()
				.findViewById(android.R.id.content)).addView(dynamicAdView);

		dbHandler = DatabaseHandler.getInstance();
		dbHandler.setContext(context);

		activity.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Commons.DISPLAY_MESSAGE_ACTION));

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					GCMRegistrar.register(activity.getApplicationContext(),
							Commons.SENDER_ID);
				} catch (Exception e) {
					Log.e("AdContentLoader", e.toString());
				}
			}
		}).start();
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
//				File dir = Environment.getExternalStorageDirectory();
//				File myFile = new File(dir, fileName);
//
//				BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
//				bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
//				Bitmap myBitmap2 = BitmapFactory.decodeFile(
//						myFile.getAbsolutePath(), bitmapFatoryOptions);

				dynamicAdView.UpdateImage(BitmapFactory.decodeResource(activity.getResources(), R.drawable.ad_image));
			}
		});
	}

	// Mising database query
	public void adUpdate(int adIdentifier) {
		final String adFilePath = dbHandler.getInstance().getDatabaseManager()
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