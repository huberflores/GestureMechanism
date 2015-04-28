package com.in.mobile.gesture.ad;

import java.util.Calendar;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

public class DynamicAdView extends RelativeLayout {

	public enum Position {
		TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_LEFT, CENTER, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT, COORDINATES
	}

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private int startingX;
	private int startingY;
	private int startingWidth;
	private int startingHeight;
	private int maxWidth;
	private int maxHeight;
	private int zoomStartingX;
	private int zoomStartingY;
	private int zoomStartingWidth;
	private int zoomStartingHeight;

	private int mode = NONE;

	private float lastTouchX;
	private float lastTouchY;
	private float posX;
	private float posY;
	private float oldDist;
	private float newDist;

	private long gestureStartTime;

	private boolean isFullScreen = false;
	private boolean touchesEnabled = true;

	public Position position;

	Ad ad;

	ImageView image;

	public DynamicAdView(Context context, int maxWidth, int maxHeight) {

		super(context);

		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;

		LayoutParams layout = new LayoutParams(0, 0);

		LayoutParams imageLayout = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		imageLayout.addRule(CENTER_IN_PARENT);

		image = new ImageView(context);
		image.setScaleType(ScaleType.MATRIX);

		addView(image, imageLayout);

		setLayoutParams(layout);
		setVisibility(INVISIBLE);

		posX = getX();
		posY = getY();
	}

	public Position getPosition() {
		return this.position;
	}

	public void setPosition(Position position) {
		this.position = position;

		if (position == Position.TOP_LEFT) {
			setX(0);
			setY(0);
		} else if (position == Position.TOP_CENTER) {
			setX((maxWidth - getWidth()) / 2);
			setY(0);
		} else if (position == Position.TOP_RIGHT) {
			setX(maxWidth - getWidth());
			setY(0);
		} else if (position == Position.CENTER_LEFT) {
			setX(0);
			setY((maxHeight - getHeight()) / 2);
		} else if (position == Position.CENTER) {
			setX((maxWidth - getWidth()) / 2);
			setY((maxHeight - getHeight()) / 2);
		} else if (position == Position.CENTER_RIGHT) {
			setX(maxWidth - getWidth());
			setY((maxHeight - getHeight()) / 2);
		} else if (position == Position.BOTTOM_LEFT) {
			setX(0);
			setY(maxHeight - getHeight());
		} else if (position == Position.BOTTOM_CENTER) {
			setX((maxWidth - getWidth()) / 2);
			setY(maxHeight - getHeight());
		} else if (position == Position.BOTTOM_RIGHT) {
			setX(maxWidth - getWidth());
			setY(maxHeight - getHeight());
		}
	}

	public void setPosition(int x, int y) {
		this.position = Position.COORDINATES;
		this.startingX = x;
		this.startingY = y;

		setX(x);
		setY(y);
	}

	@Override
	public boolean performClick() {
		if (!isFullScreen) {
			animateToFullScreen();
		}

		return super.performClick();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!touchesEnabled) {
			return false;
		}

		final int action = MotionEventCompat.getActionMasked(event);

		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			gestureStartTime = Calendar.getInstance().getTimeInMillis();

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
				Log.e("DynamicAdView", "Drag");

				if (isFullScreen) {
					setX(posX);
				} else {
					setX(posX);
					setY(posY);
				}

			} else if (mode == ZOOM) {
				newDist = getTouchSpacing(event);

				if (newDist > 10f) {
					Log.e("DynamicAdView ", "Zoom");

					float zoomInScale = (newDist / oldDist) + 1;
					float zoomOutScale = (newDist / oldDist);

					if (newDist - oldDist > 200) {
						animateToFullScreen();
						break;
					} else if (newDist - oldDist < -200) {
						animateToOriginal(
								(int) (zoomStartingWidth * zoomOutScale),
								(int) (zoomStartingHeight * zoomOutScale));
						break;
					} else if (newDist - oldDist > 0) {
						if (zoomStartingWidth * zoomInScale < maxWidth) {
							getLayoutParams().width = (int) (zoomStartingWidth * zoomOutScale);
						}
						if (zoomStartingHeight * zoomInScale < maxHeight) {
							getLayoutParams().height = (int) (zoomStartingHeight * zoomOutScale);
						}
					} else if (newDist - oldDist < 0) {
						if (zoomStartingWidth * zoomOutScale > startingWidth) {
							getLayoutParams().width = (int) (zoomStartingWidth * zoomOutScale);
						}
						if (zoomStartingHeight * zoomOutScale > startingHeight) {
							getLayoutParams().height = (int) (zoomStartingHeight * zoomOutScale);
						}
					}

					// float scale = newDist / oldDist;
					// setScaleX(scale);
					// setScaleY(scale);
				}
			}

			break;
		}

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: {
			if (Calendar.getInstance().getTimeInMillis() - gestureStartTime < 100) {
				Log.e("DynamicAdView ", "Click");

				performClick();
			} else if ((isFullScreen && posX < -(maxWidth / 3))
					|| (!isFullScreen && posX < 0)) {
				dislike();
			} else if ((isFullScreen && posX > maxWidth / 3)
					|| (!isFullScreen && posX > maxWidth
							- getLayoutParams().width)) {
				like();
			} else if (isFullScreen) {
				animateToFullScreen();
			}

			break;
		}

		case MotionEvent.ACTION_POINTER_DOWN: {
			oldDist = getTouchSpacing(event);

			if (oldDist > 10f) {
				mode = ZOOM;

				zoomStartingWidth = getLayoutParams().width;
				zoomStartingHeight = getLayoutParams().height;
				zoomStartingX = (int) getX();
				zoomStartingY = (int) getY();
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

		touchesEnabled = false;

		Animation a = new Animation() {

			int currentWidth = getLayoutParams().width;
			int currentHeight = getLayoutParams().height;

			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {

				getLayoutParams().width = currentWidth
						+ (int) ((maxWidth - currentWidth) * interpolatedTime);
				getLayoutParams().height = getLayoutParams().height = currentHeight
						+ (int) ((maxHeight - currentHeight) * interpolatedTime);

				if (!isFullScreen) {
					setY(posY - (int) (posY * interpolatedTime));
				}

				setX(posX - (int) (posX * interpolatedTime));

				requestLayout();

				if (ad.getLargeImageBmp() != null) {
					if (getLayoutParams().height >= ad.getLargeImageBmp()
							.getHeight()
							&& getLayoutParams().width >= ad.getLargeImageBmp()
									.getWidth()) {

						setBackgroundColor(getDominantColor(ad
								.getLargeImageBmp()));

						image.setImageBitmap(ad.getLargeImageBmp());
					}
				}
			}
		};

		a.setDuration(1000);
		a.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				touchesEnabled = true;
				isFullScreen = true;

				posX = getX();
				posY = getY();
			}
		});

		startAnimation(a);
	}

	public void animateToOriginal(final int currentWidth,
			final int currentHeight) {

		touchesEnabled = false;

		Animation a = new Animation() {

			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {

				getLayoutParams().width = currentWidth
						+ (int) ((startingWidth - currentWidth) * interpolatedTime);
				getLayoutParams().height = currentHeight
						+ (int) ((startingHeight - currentHeight) * interpolatedTime);

				requestLayout();

				setX(zoomStartingX * interpolatedTime);
				setY(zoomStartingY * interpolatedTime);

				if (ad.getLargeImageBmp() != null) {
					if (getLayoutParams().height < ad.getLargeImageBmp()
							.getHeight()
							|| getLayoutParams().width < ad.getLargeImageBmp()
									.getWidth()) {

						if (ad.getSmallImageBmp() != null) {
							setBackgroundColor(getDominantColor(ad
									.getSmallImageBmp()));

							image.setImageBitmap(ad.getSmallImageBmp());
						}
					}
				}
			}
		};

		a.setDuration(1000);
		a.setFillAfter(true);
		a.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				touchesEnabled = true;
				isFullScreen = false;

				posX = getX();
				posY = getY();
			}
		});

		startAnimation(a);
	}

	void animateLeftOffScreen() {
		touchesEnabled = false;

		Animation a = new Animation() {

			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {

				setX(posX - (int) ((maxWidth * 2 - posX) * interpolatedTime));
			}
		};

		a.setDuration(500);
		a.setFillAfter(true);
		a.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				touchesEnabled = true;

				reset();
			}
		});

		startAnimation(a);
	}

	void animateRightOffScreen() {
		touchesEnabled = false;

		Animation a = new Animation() {

			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {

				setX(posX + (int) ((maxWidth - posX) * interpolatedTime));
			}
		};

		a.setDuration(500);
		a.setFillAfter(true);
		a.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				touchesEnabled = true;

				reset();
			}
		});

		startAnimation(a);
	}

	public void UpdateImage(Ad ad) {
		this.ad = ad;

		if (position == null) {
			setX(0);
			setY(0);
		} else if (position == Position.COORDINATES) {
			setX(startingX);
			setY(startingY);
		} else {
			setPosition(position);
		}

		posX = getX();
		posY = getY();

		if (ad.getSmallImageBmp() != null) {
			this.startingWidth = ad.getSmallImageBmp().getWidth();
			this.startingHeight = ad.getSmallImageBmp().getHeight();

			setBackgroundColor(getDominantColor(ad.getLargeImageBmp()));
		} else if (ad.getLargeImageBmp() != null) {
			this.startingWidth = ad.getLargeImageBmp().getWidth();
			this.startingHeight = ad.getLargeImageBmp().getHeight();

			setBackgroundColor(getDominantColor(ad.getLargeImageBmp()));
		} else {
			this.startingWidth = 0;
			this.startingHeight = 0;
		}

		getLayoutParams().width = startingWidth;
		getLayoutParams().height = startingHeight;

		requestLayout();

		setVisibility(VISIBLE);

		image.setImageBitmap(ad.getSmallImageBmp());
		image.invalidate();

		isFullScreen = false;
	}

	int getDominantColor(Bitmap bmp) {
		Bitmap onePixelBitmap = Bitmap.createScaledBitmap(bmp, 1, 1, true);

		int pixel = onePixelBitmap.getPixel(0, 0);

		int red = Color.red(pixel);
		int green = Color.green(pixel);
		int blue = Color.blue(pixel);

		return Color.rgb(red, green, blue);
	}

	void like() {
		Log.e("DynamicAdView", "Like");

		animateRightOffScreen();
	}

	void dislike() {
		Log.e("DynamicAdView", "Dislike");

		animateLeftOffScreen();
	}

	void reset() {
		setVisibility(INVISIBLE);

		image.setImageBitmap(null);

		animateToOriginal(maxWidth, maxHeight);
	}
}