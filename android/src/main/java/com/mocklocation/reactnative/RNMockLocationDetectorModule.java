package com.mocklocation.reactnative;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class RNMockLocationDetectorModule extends ReactContextBaseJavaModule {

    public RNMockLocationDetectorModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNMockLocationDetector";
    }

    @ReactMethod
    public void checkMockLocationProvider(final Promise promise) {
        if (ActivityCompat.checkSelfPermission(getCurrentActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getCurrentActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            promise.resolve("PERMISSION_REJECTED");
        }
        FusedLocationProviderClient mFusedLocationClient;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getCurrentActivity());
        mFusedLocationClient.getLastLocation().addOnSuccessListener(getCurrentActivity(),
            new OnSuccessListener < Location > () {
                @Override
                public void onSuccess(Location location) {
                    if (location == null) {
                        promise.resolve("LOCATION_ERROR");
                    }
                    else if (isLocationFromMockProvider(getCurrentActivity(), location)) {
                        promise.resolve("MOCK");
                    } else{
                        promise.resolve("DEVICE");
                    }
                }
            });
    }

    public boolean isLocationFromMockProvider(Context context, Location location) {
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            return location.isFromMockProvider();
        }
        if (Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
            return false;
        }
        return true;
    }
}