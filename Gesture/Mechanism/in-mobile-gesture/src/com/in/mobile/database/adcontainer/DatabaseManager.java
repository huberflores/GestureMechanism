/*
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * Please send inquiries to huber AT ut DOT ee
 * 
 * @author Huber Flores
 * 
 */

package com.in.mobile.database.adcontainer;

import com.in.mobile.gesture.ad.contentprovider.*;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public class DatabaseManager {

	private Uri dbUri;
	private Context context;

	public DatabaseManager(Context c) {
		this.context = c;
	}

	public void saveData(String adName, String adUrl, double priority,
			double timeToLive) {
		ContentValues values = new ContentValues();
		values.put(AdDescriptor.COLUMN_AD_NAME, adName);
		values.put(AdDescriptor.COLUMN_AD_URL, adUrl);
		values.put(AdDescriptor.COLUMN_AD_PRIORITY, priority);
		values.put(AdDescriptor.COLUMN_AD_TIMETOLIVE, timeToLive);

		dbUri = context.getContentResolver().insert(
				AdContentProvider.CONTENT_URI, values);
	}

	public void setDbUri(Uri cursor) {
		this.dbUri = cursor;
	}

	public String getAdFilePath(int adIdentifier) {
		return "";
	}
}