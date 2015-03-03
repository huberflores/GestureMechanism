package com.in.mobile.gesture.ad;

import java.io.File;

import android.util.Log;
import android.widget.ImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Environment;
import android.widget.ImageView;

public class DynamicAdView extends ImageView {

	private static final String TAG = "DynamicAd";

	private int imageWidth, imageHeight;

	public DynamicAdView(Context context, int width, int height) {
		
		super(context);
		
		imageWidth = width;
		imageHeight = height;
	}

	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);

		// "RECREATE" THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		Paint myPaint = new Paint();

		if (AdContentLoader.SHOWFLAG) {
			Log.i(TAG, "long ad");
			AdContentLoader.adUpdate("LongAd.jpeg");
		} else {
			Log.i(TAG, "small ad");
		}
	}
}