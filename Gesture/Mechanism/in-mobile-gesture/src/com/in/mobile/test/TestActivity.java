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

import com.in.mobile.gesture.ad.AdContentLoader;
import com.in.mobile.gesture.ad.R;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class TestActivity extends ActionBarActivity {

	AdContentLoader adLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_test);

		adLoader = new AdContentLoader(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		adLoader.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		adLoader.destroy();
	}
}