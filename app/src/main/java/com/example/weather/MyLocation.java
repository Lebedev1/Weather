package com.example.weather;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


class MyLocation implements LocationListener {

    static Location imHere; // здесь будет всегда доступна самая последняя информация о местоположении пользователя.
    LocationManager locationManager;
    LocationListener locationListener;

    public void SetUpLocationListener(Context context) // это нужно запустить в самом начале работы программы
    {
        locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new MyLocation();
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
            imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        else
            {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                    locationListener);
            imHere = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }


    }

    @Override
    public void onLocationChanged(Location loc) {
        imHere = loc;
    }
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void stopUpdate(){
          try{      locationManager.removeUpdates(locationListener); } catch (NullPointerException e){}
    }
}
