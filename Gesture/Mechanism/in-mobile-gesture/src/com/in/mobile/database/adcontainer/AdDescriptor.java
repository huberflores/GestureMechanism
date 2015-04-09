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

import android.database.sqlite.SQLiteDatabase;

public class AdDescriptor {

	// Table
	public static final String TABLE_TIMESTAMP = "files";
	// Attributes
	public static final String COLUMN_AD_ID = "_id";
	public static final String COLUMN_AD_NAME = "ad_name";
	public static final String COLUMN_AD_URL = "ad_url";
	public static final String COLUMN_AD_PRIORITY = "ad_priority";
	public static final String COLUMN_AD_TIMETOLIVE = "ad_timetolive";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_TIMESTAMP + "(" + COLUMN_AD_ID
			+ " integer primary key autoincrement, " + COLUMN_AD_NAME
			+ " text not null, " + COLUMN_AD_URL + " text not null, "
			+ COLUMN_AD_PRIORITY + " real not null, " + COLUMN_AD_TIMETOLIVE
			+ " real not null " + ");";

	// Database creation
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMESTAMP);
		onCreate(database);
	}
}