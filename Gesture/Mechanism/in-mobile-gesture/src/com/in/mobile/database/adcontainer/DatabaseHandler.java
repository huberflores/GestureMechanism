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

import android.content.Context;
import android.net.Uri;

public class DatabaseHandler {

	public static DatabaseHandler instance;
	
	private static DatabaseManager dbManager;

	private Uri dbUri;

	private DatabaseHandler() {
	}

	public static synchronized DatabaseHandler getInstance() {
		if (instance == null) {
			instance = new DatabaseHandler();
		}

		return instance;
	}

	public void setContext(Context context) {
		dbManager = new DatabaseManager(context);
		dbManager.setDbUri(dbUri);
	}

	public static DatabaseManager getDatabaseManager() {
		return dbManager;
	}
}