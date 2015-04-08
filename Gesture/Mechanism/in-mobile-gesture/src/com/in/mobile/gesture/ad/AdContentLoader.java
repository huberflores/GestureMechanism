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
import com.in.mobile.database.adcontainer.DatabaseHandler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class AdContentLoader implements OnTouchListener {

	private static final String TAG = "AdContentLoader";
	private static boolean SHOULDUPDATEIMAGE;
	public static float IMAGEHEIGHT;
	public static float IMAGEWIDTH;
	public static int SCREENWIDTH;
	public static int SCREENHEIGHT;
	public static boolean SHOWFLAG = false;

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

	private Bitmap myBitmap;

	DatabaseHandler dataAds;

	public AdContentLoader(FrameLayout frameLayout, Point size, Context context) {
		SCREENWIDTH = size.x;
		SCREENHEIGHT = size.y;

		matrix.setTranslate(1f, 1f);
		matrix.setScale(0.1f, 0.1f);

		dynamicAdView = new DynamicAdView(context, 200, 200, SCREENWIDTH,
				SCREENHEIGHT, "ad.jpg", Color.rgb(255, 0, 0));
//		dynamicAdView.setOnTouchListener(this);

		frameLayout.addView(dynamicAdView);

		dataAds = DatabaseHandler.getInstance();
		dataAds.setContext(context);
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
}