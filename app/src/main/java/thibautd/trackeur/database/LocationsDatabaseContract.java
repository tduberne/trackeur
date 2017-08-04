package thibautd.trackeur.database;

import android.provider.BaseColumns;

public class LocationsDatabaseContract {
	private LocationsDatabaseContract() {}

	public static class Location implements BaseColumns {
		private Location() {}
		public static final String TABLE_NAME = "locations";
		public static final String COLUMN_NAME_LONG = "longitude";
		public static final String COLUMN_NAME_LAT = "lat";
		public static final String COLUMN_NAME_TIME = "time";
		public static final String COLUMN_NAME_ACC = "accuracy";
		public static final String COLUMN_NAME_ALT = "altitude";
		public static final String COLUMN_NAME_PROV = "provider";
	}

	public static class RecordingEvent implements BaseColumns {
		private RecordingEvent() {}
		public static final String TABLE_NAME = "recordingEvents";
		public static final String COLUMN_NAME_TYPE = "eventType";
		public static final String COLUMN_NAME_TIME = "eventTime";
	}
}
