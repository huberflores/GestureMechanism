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

import com.google.android.gcm.GCMRegistrar;
import com.in.mobile.common.utilities.Commons;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;

public class AdContentLoader {

	static Activity activity;

	static DynamicAdView dynamicAdView;

	private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			intent.getExtras().getString(Commons.EXTRA_MESSAGE);
		}
	};

	public AdContentLoader(Activity context) {

		activity = context;

		Point screenSize = new Point();

		activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

		GCMRegistrar.checkManifest(activity);

		dynamicAdView = new DynamicAdView(context, screenSize.x, screenSize.y);

		((ViewGroup) activity.getWindow().getDecorView()
				.findViewById(android.R.id.content)).addView(dynamicAdView);

		activity.registerReceiver(messageReceiver, new IntentFilter(
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
		activity.unregisterReceiver(messageReceiver);

		GCMRegistrar.onDestroy(activity);
	}

	public static void adUpdate(final Ad ad) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			public void run() {
				dynamicAdView.UpdateImage(ad);
			}
		});
	}
}