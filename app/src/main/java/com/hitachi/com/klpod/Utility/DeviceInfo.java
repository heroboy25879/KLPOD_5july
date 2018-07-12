package com.hitachi.com.klpod.Utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import android.provider.Settings;
import android.provider.Settings.System;


public class DeviceInfo {

    private Context context;
    private String latitude;
    private String longitude;

    public DeviceInfo(Context context) {
        this.context = context;


    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String IMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        return telephonyManager.getDeviceId();
    }

//    public  void getLocation()
//    {
//        LocationManager locationManager;
//        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//        if(location != null)
//        {
//            latitude = String.valueOf(location.getLatitude());
//            longitude = String.valueOf(location.getLongitude());
//        }
//
//
//    }

    private LocationManager locationManager;  //1
    private Criteria criteria;//2


    public void setupGPS()
    {
        locationManager = (LocationManager) context.getSystemService((Context.LOCATION_SERVICE));
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false); //ไม่หาพิกัดความสูงจากระดับน้ำทะเล
        updateGPS();

    }

    public void updateGPS() {
        //หาพิกัดจาก network
        Location networkLocation = myFindLocation(LocationManager.NETWORK_PROVIDER);
        if (networkLocation != null) {
            latitude = String.valueOf(networkLocation.getLatitude());
            longitude = String.valueOf(networkLocation.getLongitude());
        }

        //หาพิกัดจาก PGS Card
        Location gpsLocation = myFindLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            latitude = String.valueOf(gpsLocation.getLatitude());
            longitude = String.valueOf(gpsLocation.getLongitude());
        }
    }

    //6
    public Location myFindLocation(String strProvider) {

        Location location = null;
        //เช็คการ์ด GPS
        if (locationManager.isProviderEnabled(strProvider)) {
            //7
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            locationManager.requestLocationUpdates(strProvider, 1000, 1, locationListener);
            //9
            location = locationManager.getLastKnownLocation(strProvider);
        }
        return location;
    }

    //เมื่อมีการเปลี่ยนพิกัด class นี้จะทำงาน //4
    public LocationListener locationListener = new LocationListener() {
        @Override

        public void onLocationChanged(Location location) {
            //5
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void  stopGPS()
    {
        locationManager.removeUpdates(locationListener);
    }


}
