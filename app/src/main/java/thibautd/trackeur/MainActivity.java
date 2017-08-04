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
		final LocationRequest locationRequest = new LocationRequest();
		// get updates only when the device actually checks location
		locationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);

		// check the settings allow us to perform the tracking we want to
		// TODO: check this does not activates GPS if it is deactivated
		final SettingsClient settingsClient = LocationServices.getSettingsClient(this);
		final Task<LocationSettingsResponse> settingsResponseTask =
				settingsClient.checkLocationSettings(
						new LocationSettingsRequest.Builder()
								.addLocationRequest(locationRequest)
								.build());

		settingsResponseTask.addOnSuccessListener(
				this,
				new OnSuccessListener<LocationSettingsResponse>() {
					@Override
					public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
						// All location settings are satisfied. The client can initialize
						// location requests here.

						if (ActivityCompat.checkSelfPermission(
								MainActivity.this,
								Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
							ActivityCompat.checkSelfPermission(
									MainActivity.this,
									Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
							// TODO: Consider calling
							//    ActivityCompat#requestPermissions
							// here to request the missing permissions, and then overriding
							//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
							//                                          int[] grantResults)
							// to handle the case where the user grants the permission. See the documentation
							// for ActivityCompat#requestPermissions for more details.
							return;
						}
						locationProviderClient.requestLocationUpdates(
								locationRequest,
								new LocationCallback() {
									@Override
									public void onLocationResult(LocationResult locationResult) {
										for (Location l : locationResult.getLocations()) {
											handleLocation(l);
										}
									}
								},
								null);
					}
				});

		settingsResponseTask.addOnFailureListener(
				this,
				new OnFailureListener() {
					@Override
					public void onFailure( @NonNull Exception e) {
						int statusCode = ((ApiException) e).getStatusCode();
						switch (statusCode) {
							case CommonStatusCodes.RESOLUTION_REQUIRED:
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
