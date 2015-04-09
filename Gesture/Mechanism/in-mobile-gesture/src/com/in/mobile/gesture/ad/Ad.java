package com.in.mobile.gesture.ad;

import android.graphics.Bitmap;

public class Ad {
	
	String id;
	String smallImageUrl;
	String largeImageUrl;
	Bitmap smallImageBmp;
	Bitmap largeImageBmp;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSmallImageUrl() {
		return smallImageUrl;
	}
	
	public void setSmallImageUrl(String smallImageUrl) {
		this.smallImageUrl = smallImageUrl;
	}
	
	public String getLargeImageUrl() {
		return largeImageUrl;
	}
	
	public void setLargeImageUrl(String largeImageUrl) {
		this.largeImageUrl = largeImageUrl;
	}
	
	public Bitmap getSmallImageBmp() {
		return smallImageBmp;
	}

	public void setSmallImageBmp(Bitmap smallImageBmp) {
		this.smallImageBmp = smallImageBmp;
	}

	public Bitmap getLargeImageBmp() {
		return largeImageBmp;
	}

	public void setLargeImageBmp(Bitmap largeImageBmp) {
		this.largeImageBmp = largeImageBmp;
	}
}