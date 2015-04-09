package com.in.mobile.gesture.ad;

import java.io.File;
import android.os.Environment;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DynamicAdView extends FrameLayout {

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private int startingWidth;
	private int startingHeight;
	private int maxWidth;
	private int maxHeight;
	private int zoomStartingWidth;
	private int zoomStartingHeight;
	private int mode = NONE;
	private float lastTouchX;
	private float lastTouchY;
	private float posX;
	private float posY;
	private float oldDist;
	private float newDist;

	private ImageView image;

	public DynamicAdView(Context context, int startingWidth,
			int startingHeight, int maxWidth, int maxHeight, String imagePath,
			int backgroundColor) {

		super(context);

		this.startingWidth = startingWidth;
		this.startingHeight = startingHeight;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				startingWidth, startingHeight);
		params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

		image = new ImageView(context);
		image.setScaleType(ScaleType.MATRIX);
		image.setLayoutParams(params);

		addView(image);

		File dir = Environment.getExternalStorageDirectory();
		File imageFile = new File(dir, imagePath);

		if (imageFile.exists()) {
			BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
			bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;

			Bitmap bitmap = BitmapFactory.decodeFile(
					imageFile.getAbsolutePath(), bitmapFatoryOptions);

			image.setImageBitmap(bitmap);
		}

		setLayoutParams(params);
		setBackgroundColor(backgroundColor);

		posX = getX();
		posY = getY();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		final int action = MotionEventCompat.getActionMasked(event);

		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			final float x = event.getRawX();
			final float y = event.getRawY();

			lastTouchX = x;
			lastTouchY = y;

			mode = DRAG;

			break;
		}

		case MotionEvent.ACTION_MOVE: {

			final float x = event.getRawX();
			final float y = event.getRawY();

			final float dx = x - lastTouchX;
			final float dy = y - lastTouchY;

			posX += dx;
			posY += dy;

			lastTouchX = x;
			lastTouchY = y;

			if (mode == DRAG) {
				Log.e("DynamicAdView - onTouchEvent", "Drag");

				setX(posX);
				setY(posY);
			} else if (mode == ZOOM) {
				newDist = getTouchSpacing(event);

				if (newDist > 10f) {
					Log.e("DynamicAdView - onTouchEvent", "Zoom");

					float zoomInScale = (newDist / oldDist) + 1;
					float zoomOutScale = (newDist / oldDist);

					ViewGroup.LayoutParams lp = getLayoutParams();

					if (newDist - oldDist > 200) {
						animateToFullScreen();
						break;
					} else if (newDist - oldDist < -200) {
						animateToOriginal();
						break;
					} else if (newDist - oldDist > 0) {
						if (zoomStartingWidth * zoomInScale < maxWidth) {
							lp.width = (int) (zoomStartingWidth * zoomOutScale);
						}
						if (zoomStartingHeight * zoomInScale < maxHeight) {
							lp.height = (int) (zoomStartingHeight * zoomOutScale);
						}
					} else if (newDist - oldDist < 0) {
						if (zoomStartingWidth * zoomOutScale > startingWidth) {
							lp.width = (int) (zoomStartingWidth * zoomOutScale);
						}
						if (zoomStartingHeight * zoomOutScale > startingHeight) {
							lp.height = (int) (zoomStartingHeight * zoomOutScale);
						}
					}

					setLayoutParams(lp);

					// float scale = newDist / oldDist;
					// setScaleX(scale);
					// setScaleY(scale);
				}
			}

			break;
		}

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: {
			break;
		}

		case MotionEvent.ACTION_POINTER_DOWN: {
			oldDist = getTouchSpacing(event);

			if (oldDist > 10f) {
				mode = ZOOM;

				zoomStartingWidth = getLayoutParams().width;
				zoomStartingHeight = getLayoutParams().height;
			}

			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			mode = NONE;

			break;
		}
		}

		return true;
	}

	private float getTouchSpacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);

		return (float) Math.sqrt(x * x + y * y);
	}

	public void animateToFullScreen() {
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				super.applyTransformation(interpolatedTime, t);

				getLayoutParams().width = maxWidth;
				getLayoutParams().height = maxHeight;

				requestLayout();

				setX(0);
				setY(0);

				posX = 0;
				posY = 0;
			}

			@Override
			public void initialize(int width, int height, int parentWidth,
					int parentHeight) {
				super.initialize(width, height, parentWidth, parentHeight);
			}
		};

		a.setDuration(1000);
		a.setFillAfter(true);
		a.setInterpolator(new AccelerateInterpolator());
		startAnimation(a);
	}

	public void animateToOriginal() {
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				super.applyTransformation(interpolatedTime, t);

				getLayoutParams().width = startingWidth;
				getLayoutParams().height = startingHeight;

				requestLayout();

				setX((maxWidth - startingWidth) / 2);
				setY((maxHeight - startingHeight) / 2);

				posX = (maxWidth - startingWidth) / 2;
				posY = (maxHeight - startingHeight) / 2;
			}

			@Override
			public void initialize(int width, int height, int parentWidth,
					int parentHeight) {
				super.initialize(width, height, parentWidth, parentHeight);
			}
		};

		a.setDuration(1000);
		a.setFillAfter(true);
		a.setInterpolator(new AccelerateInterpolator());
		startAnimation(a);
	}

	public void UpdateImage(Bitmap bmp) {
		image.setImageBitmap(bmp);
		image.invalidate();
	}
}