package com.migestion.migestion.Servicio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.migestion.migestion.Inicio;

public class LocationFragment extends Fragment implements LocationListener {

    private static final String LOG = "LocationFragment: ";
    private LocationManager mLocationManager;

    public LocationFragment() {
        Log.i(LOG, "LocationFragment: ");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG, "onResume");
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG, "onPause");
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Inicio.coordenadasLAT = String.valueOf(location.getLatitude());
        Inicio.coordenadasLNG = String.valueOf(location.getLongitude());
        Inicio.coordenadasTime = location.getTime();
        Log.i(LOG, "LAT: " + Inicio.coordenadasLAT);
        Log.i(LOG, "LNG: " + Inicio.coordenadasLNG);
        Log.i(LOG, "TIME: " + Inicio.coordenadasTime);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(LOG, "Provider " + provider + " has now status: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(LOG, "Provider " + provider + " is enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(LOG, "Provider " + provider + " is disabled");
    }
}