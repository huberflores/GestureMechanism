package com.in.mobile.gesture.ad;

import android.content.Context;
import com.google.android.gcm.GCMBroadcastReceiver;

public class BroadcastReceiver extends GCMBroadcastReceiver {

	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		return "com.in.mobile.gesture.ad.GCMIntentService";
	}
}