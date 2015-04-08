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

package com.in.mobile.test;

import java.io.IOException;
import com.in.mobile.common.utilities.Commons;
import com.in.mobile.database.adcontainer.DatabaseCommons;
import com.in.mobile.gesture.ad.AdContentLoader;
import com.in.mobile.gesture.ad.R;
import com.google.android.gcm.GCMRegistrar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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

public class TestActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_test);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		new AdContentLoader((FrameLayout) getWindow().getDecorView()
				.findViewById(android.R.id.content), size, this);

		GCMRegistrar.checkManifest(this);

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Commons.DISPLAY_MESSAGE_ACTION));

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					GCMRegistrar.register(getApplicationContext(),
							Commons.SENDER_ID);
				} catch (Exception e) {
					Log.e("AdMechanism - onCreate", e.toString());
				}
			}
		});

		thread.start();
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

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(
					Commons.EXTRA_MESSAGE);

		}
	};

	public void extractDatabaseFile(DatabaseCommons db) {
		try {
			db.copyDatabaseFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}