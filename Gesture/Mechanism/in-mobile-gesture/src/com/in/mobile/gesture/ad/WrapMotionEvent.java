package com.in.mobile.gesture.ad;

import android.view.MotionEvent;

public class WrapMotionEvent {
	protected MotionEvent event;

	protected WrapMotionEvent(MotionEvent event) {
		this.event = event;
	}

	static public WrapMotionEvent wrap(MotionEvent event) {
		return new WrapMotionEvent(event);
	}

	public int getAction() {
		return event.getAction();
	}

	public float getX() {
		return event.getX();
	}

	public float getX(int pointerIndex) {
		return event.getX(pointerIndex);
	}

	public float getY() {
		return event.getY();
	}

	public float getY(int pointerIndex) {
		return event.getY(pointerIndex);
	}

	public int getPointerCount() {
		return event.getPointerCount();
	}

	public int getPointerId(int pointerIndex) {
		return event.getPointerId(pointerIndex);
	}
}