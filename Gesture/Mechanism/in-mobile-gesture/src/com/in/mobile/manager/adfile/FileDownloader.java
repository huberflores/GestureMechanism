/*
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * Please send inquiries to huber AT ut DOT ee
 *
 * author Huber Flores
 * in-mobile, 2014
 */

package com.in.mobile.manager.adfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.in.mobile.database.adcontainer.DatabaseHandler;
import com.in.mobile.gesture.ad.Ad;
import com.in.mobile.gesture.ad.AdContentLoader;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FileDownloader extends AsyncTask<Ad, Void, Ad> {

	Context context;

	public FileDownloader(Context context) {
		this.context = context;
	}

	@Override
	protected Ad doInBackground(Ad... ads) {
		Log.e("FileDownloader", "Downloading Images");

		Ad ad = ads[0];

		try {
			Log.e("", "1");
			URL url = new URL(ad.getSmallImageUrl());
			Log.e("", "2");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			Log.e("", "3");
			connection.setRequestMethod("GET");
			Log.e("", "4");
			connection.connect();
			Log.e("", "5");

			InputStream inputStream = connection.getInputStream();
			Log.e("", "6");

			ad.setSmallImageBmp(BitmapFactory.decodeStream(inputStream));
			Log.e("", "7");
		} catch (Exception e) {
			Log.e("FileDownloader",
					"Error downloading small image: " + e.toString());
		}

		try {
			URL url = new URL(ad.getLargeImageUrl());
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			InputStream inputStream = connection.getInputStream();

			ad.setLargeImageBmp(BitmapFactory.decodeStream(inputStream));
		} catch (Exception e) {
			Log.e("FileDownloader",
					"Error downloading small image: " + e.toString());
		}

		Log.e("FileDownloader", "Image download finished");

		return ad;
	}

	@Override
	protected void onPostExecute(Ad result) {
		Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
		AdContentLoader.adUpdate(result);
	}
}