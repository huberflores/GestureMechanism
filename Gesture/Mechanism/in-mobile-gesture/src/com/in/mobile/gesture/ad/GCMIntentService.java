/*
 * author Huber Flores
 * in-mobile, 2014
 */

package com.in.mobile.gesture.ad;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.in.mobile.common.utilities.Commons;
import com.in.mobile.manager.adfile.FileDownloader;
import com.in.mobile.notification.handler.ServerUtilities;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(Commons.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i("GCMIntentService", "onRegistered");
		ServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			ServerUtilities.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			// Log.i(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {

		String smallImageUrl = intent.getStringExtra("img_small");
		String largeImageUrl = intent.getStringExtra("img_large");

		if (smallImageUrl != null) {
			Log.e(TAG, smallImageUrl);
		} else {
			Log.e(TAG, "Small image null");
		}
		if (largeImageUrl != null) {
			Log.e(TAG, largeImageUrl);
		} else {
			Log.e(TAG, "Large image null");
		}

		Log.e(Commons.TAG, "Message received from server");
		FileDownloader task = new FileDownloader(getApplicationContext());
		task.execute(new String[] { smallImageUrl, largeImageUrl });
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		String message = "Message deleted from the bar";

		// generate message in notification bar
		// generateNotification(context, message);
	}

	@Override
	public void onError(Context context, String errorId) {

	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}

	private static void generateNotification(Context context, String message,
			Class<?> activityClass) {
		int icon = 0;
		long when = System.currentTimeMillis();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = "recibido del servidor";
		Intent notificationIntent = new Intent(context, activityClass);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
	}
}