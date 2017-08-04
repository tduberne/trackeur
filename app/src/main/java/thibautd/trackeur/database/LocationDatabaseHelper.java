package thibautd.trackeur.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static thibautd.trackeur.database.LocationsDatabaseContract.*;

public class LocationDatabaseHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "Locations.db";

	public LocationDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				"CREATE TABLE "+Location.TABLE_NAME +" ( "+
						Location._ID + "INTEGER PRIMARY KEY " +
						Location.COLUMN_NAME_TIME +" REAL " +
						Location.COLUMN_NAME_LONG +" REAL " +
						Location.COLUMN_NAME_LAT +" REAL " +
						Location.COLUMN_NAME_ACC +" REAL " +
						Location.COLUMN_NAME_ALT +" REAL " +
						Location.COLUMN_NAME_PROV +" TEXT )" );
		db.execSQL(
				"CREATE TABLE "+RecordingEvent.TABLE_NAME+" ( "+
						RecordingEvent._ID + "INTEGER PRIMARY KEY " +
						RecordingEvent.COLUMN_NAME_TIME +" REAL " +
						RecordingEvent.COLUMN_NAME_TYPE +" TEXT )" );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// first approach for testing: just delete table if version changes
		clearDatabase( db );
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// first approach for testing: just delete table if version changes
		clearDatabase( db );
	}

	public static void clearDatabase( final SQLiteDatabase db ) {
		db.execSQL( "DROP TABLE IF EXISTS "+Location.TABLE_NAME );
		db.execSQL( "DROP TABLE IF EXISTS "+RecordingEvent.TABLE_NAME );
	}
}
