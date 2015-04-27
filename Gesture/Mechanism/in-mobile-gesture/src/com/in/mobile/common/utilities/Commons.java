package com.in.mobile.common.utilities;

import android.content.Context;
import android.content.Intent;

public class Commons {

	public static final String TAG = "GCMPushNotification";

	// public static final String SENDER_ID = "662XXXXXXXXX";
	// public static final String SERVER_URL =
	// "http://192.168.1.78:8080/GCM-mavenized-server";
	
	public static final String SENDER_ID = "431447948280";
//	public static final String SERVER_URL = "http://172.17.169.218:8080/Back-end_Gesture";
	public static final String SERVER_URL = "http://192.168.1.239:8080/Back-end_Gesture";

	public static final String DISPLAY_MESSAGE_ACTION = "org.wikipedia.DISPLAY_MESSAGE";
	public static final String EXTRA_MESSAGE = "message";

	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}