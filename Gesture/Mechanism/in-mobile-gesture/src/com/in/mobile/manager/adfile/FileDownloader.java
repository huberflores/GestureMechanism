/*
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * Please send inquiries to huber AT ut DOT ee
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
import com.in.mobile.gesture.ad.AdContentLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

/*
 * author Huber Flores
 * in-mobile, 2014
 */

public class FileDownloader extends AsyncTask<String, Void, String> {

	Context context;

	private String fileSdcardPath;

	DatabaseHandler dataAds;

	public FileDownloader(Context context) {
		this.context = context;
		dataAds = DatabaseHandler.getInstance();
		dataAds.setContext(context);
	}

	@Override
	protected String doInBackground(String... urls) {
		String response = "";
		for (String pathUrl : urls) {
			try {

				URL url = new URL(pathUrl);
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoOutput(true);
				urlConnection.connect();

				File SDCardRoot = Environment.getExternalStorageDirectory();
				File file = new File(SDCardRoot, "ad.jpg");

				FileOutputStream fileOutput = new FileOutputStream(file);
				InputStream inputStream = urlConnection.getInputStream();

				byte[] buffer = new byte[1024];
				int bufferLength = 0;

				while ((bufferLength = inputStream.read(buffer)) > 0) {
					fileOutput.write(buffer, 0, bufferLength);

				}

				fileSdcardPath = file.getAbsolutePath();
				fileOutput.close();

				dataAds.getInstance().getDatabaseManager()
						.saveData(fileSdcardPath, pathUrl, 1.0f, 1.0f);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return response;
	}

	@Override
	protected void onPostExecute(String result) {
		Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
		// adUpdate(fileSdcardPath);
		AdContentLoader.adUpdate("ad_image.png");

	}

	public String getFileSdcardPath() {
		return this.fileSdcardPath;
	}
}