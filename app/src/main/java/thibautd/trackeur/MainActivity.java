package thibautd.trackeur;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
	private FusedLocationProviderClient locationProviderClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

		registerLocationRequest();
	}

	private void registerLocationRequest() {
		Log.d( TAG , "creating LocationRequest" );
		final LocationRequest locationRequest = new LocationRequest();
		// get updates only when the device actually checks location
		locationRequest.setInterval( 1 );
		locationRequest.setFastestInterval( 0 );
		locationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);

		Log.d( TAG , "checking settings" );
		// check the settings allow us to perform the tracking we want to
		// TODO: check this does not activates GPS if it is deactivated
		final SettingsClient settingsClient = LocationServices.getSettingsClient(this);
		final Task<LocationSettingsResponse> settingsResponseTask =
				settingsClient.checkLocationSettings(
						new LocationSettingsRequest.Builder()
								.addLocationRequest(locationRequest)
								.build());

		settingsResponseTask.addOnSuccessListener(
				//this,
				new OnSuccessListener<LocationSettingsResponse>() {
					@Override
					public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
						// All location settings are satisfied. The client can initialize
						// location requests here.

						if (ActivityCompat.checkSelfPermission(
								MainActivity.this,
								Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
							ActivityCompat.requestPermissions(
									MainActivity.this,
									new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
									1 );
							// TODO: implement onRequestPermissionResult and handle refusal
						}
						Log.d( TAG , "Setting up location callback" );
						locationProviderClient.requestLocationUpdates(
								locationRequest,
								new LocationCallback() {
									@Override
									public void onLocationResult(LocationResult locationResult) {
										Log.d( TAG , "got location callback" );
										for (Location l : locationResult.getLocations()) {
											handleLocation(l);
										}
									}
								},
								null);
					}
				});

		settingsResponseTask.addOnFailureListener(
				//this,
				new OnFailureListener() {
					@Override
					public void onFailure( @NonNull Exception e) {
						int statusCode = ((ApiException) e).getStatusCode();
						switch (statusCode) {
							case CommonStatusCodes.RESOLUTION_REQUIRED:
								Log.e( TAG , "Resolution Required for settings failure" );
								// Location settings are not satisfied, but this can be fixed
								// by showing the user a dialog.
								try {
									// Show the dialog by calling startResolutionForResult(),
									// and check the result in onActivityResult().
									ResolvableApiException resolvable = (ResolvableApiException) e;
									resolvable.startResolutionForResult(
											MainActivity.this,
											CommonStatusCodes.RESOLUTION_REQUIRED );
								} catch (IntentSender.SendIntentException sendEx) {
									// Ignore the error.
								}
								break;
							case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
								Log.e( TAG , "settings unavailable" );
								// Location settings are not satisfied. However, we have no way
								// to fix the settings so we won't show the dialog.
								break;
						}
					}
				});
	}

	private void handleLocation(Location l) {
		Log.d( TAG , l.toString() );
	}
}
