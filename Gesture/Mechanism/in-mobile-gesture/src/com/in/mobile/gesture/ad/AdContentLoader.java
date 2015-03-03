/*
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * Please send inquiries to huber AT ut DOT ee
 */

package com.in.mobile.gesture.ad;

import java.io.File;

import com.in.mobile.database.adcontainer.DatabaseHandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/*
 * author Huber Flores
 * in-mobile, 2014
 */

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

	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

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

		dynamicAdView = new DynamicAdView(context, SCREENWIDTH, SCREENHEIGHT);

		File dir = Environment.getExternalStorageDirectory();

		File myFile = new File(dir, "ad.jpg");

		if (myFile.exists()) {
			BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
			bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
			myBitmap = BitmapFactory.decodeFile(myFile.getAbsolutePath(),
					bitmapFatoryOptions);

			dynamicAdView.setImageBitmap(myBitmap);

		} else {
			// default
			dynamicAdView.setImageResource(R.drawable.ad_image);
		}

		dynamicAdView.setScaleType(ScaleType.MATRIX);
		dynamicAdView.setImageMatrix(matrix);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
		dynamicAdView.setLayoutParams(params);
		dynamicAdView.setOnTouchListener(this);
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
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);

			start.set(event.getX(), event.getY());

			mode = DRAG;

			Log.d(TAG, "mode=DRAG");

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
				matrix.set(savedMatrix);

				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);

				Log.d(TAG, "newDist = " + newDist);

				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}

			break;
		}

		CalculateResizeSize(matrix);

		if (SHOULDUPDATEIMAGE) {
			view.setImageMatrix(matrix);
		}

		return true;
	}

	private void CalculateResizeSize(Matrix mMatrix) {
		float[] values = new float[9];
		mMatrix.getValues(values);

		newX = values[Matrix.MTRANS_X];
		newY = values[Matrix.MTRANS_Y];
		resizeWidth = values[Matrix.MSCALE_X] * IMAGEWIDTH;
		resizeHeight = values[Matrix.MSCALE_Y] * IMAGEHEIGHT;

		if (resizeWidth > SCREENWIDTH) {
			SHOWFLAG = true;
		} else {
			SHOWFLAG = false;
		}

		if (resizeWidth > SCREENWIDTH) {
			Log.e(TAG, "Width bigger than screen");
			SHOULDUPDATEIMAGE = false;
		} else if (resizeHeight > SCREENHEIGHT) {
			Log.e(TAG, "Height bigger than screen");
			SHOULDUPDATEIMAGE = false;
		} else if (newX < 0) {
			Log.e(TAG, "X < 0");
			SHOULDUPDATEIMAGE = false;
		} else if (newX + resizeWidth > SCREENWIDTH) {
			Log.e(TAG, "X > screen");
			SHOULDUPDATEIMAGE = false;
		} else if (newY < 0) {
			//TODO remove later for swipe left
			Log.e(TAG, "Y < 0");
			SHOULDUPDATEIMAGE = false;
		} else if (newY + resizeHeight > SCREENHEIGHT) {
			//TODO remove later for swipe right
			Log.e(TAG, "Y > screen");
			SHOULDUPDATEIMAGE = false;
		} else {
			Log.i(TAG, "Action OK");
			SHOULDUPDATEIMAGE = true;
		}

		Log.e(TAG, String.valueOf(newY) + " - " + String.valueOf(resizeHeight)
				+ " - " + String.valueOf(SCREENHEIGHT));

		// Log.e("TAG", "X = " + resizeWidth + "; Y = " + resizeHeight);
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
				dynamicAdView.setImageBitmap(myBitmap2);
				dynamicAdView.invalidate();
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