/*
 * author Huber Flores
 * in-mobile, 2014
 */

package com.in.mobile.gesture.ad;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.in.mobile.common.utilities.Commons;
import com.in.mobile.manager.adfile.FileDownloader;
import com.in.mobile.notification.handler.ServerUtilities;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(Commons.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.e("GCMIntentService", "Registered");
		ServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			ServerUtilities.unregister(context, registrationId);
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {

		Ad ad = new Ad();
		ad.setId(intent.getStringExtra("id"));
		ad.setSmallImageUrl(intent.getStringExtra("img_small"));
		ad.setLargeImageUrl(intent.getStringExtra("img_large"));

		Log.e("GCMIntentService", "Message received from server");
		Log.e("GCMIntentService",
				"Id: " + (ad.getId() == null ? "null" : ad.getId()));
		Log.e("GCMIntentService",
				"Small image url: " + (ad.getSmallImageUrl() == null ? "null"
						: ad.getSmallImageUrl()));
		Log.e("GCMIntentService",
				"Large image url: " + (ad.getLargeImageUrl() == null ? "null"
						: ad.getLargeImageUrl()));

		if (ad.getSmallImageUrl() != null || ad.getLargeImageUrl() != null) {
			FileDownloader task = new FileDownloader(getApplicationContext());
			task.execute(ad);
		}
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.e("GCMIntentService", "Message Deleted");
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.e("GCMIntentService", "Error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}
}