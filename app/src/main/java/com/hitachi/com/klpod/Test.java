package com.hitachi.com.klpod;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hitachi.com.klpod.Utility.DeviceInfo;

public class Test extends AppCompatActivity {

   // DeviceInfo deviceInfo = new DeviceInfo(this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        if (!checkIfAlreadyhavePermission()) {
            requestForSpecificPermission();
        }


//        DeviceInfo deviceInfo = new DeviceInfo(this);
//        deviceInfo.setupGPS();
//
//        TextView textView = findViewById(R.id.txtTest);
//        textView.setText(deviceInfo.getLatitude());

        Button button
                = findViewById(R.id.btnTest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test
                test();
            }
        });

        Button stopButton = findViewById(R.id.btnTestStop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stop
                stop();
            }
        });
    }

    private void stop() {
        DeviceInfo deviceInfo;
        deviceInfo = new DeviceInfo(this);
        deviceInfo.setupGPS();
        deviceInfo.stopGPS();
    }

    private void test() {
        DeviceInfo deviceInfo = new DeviceInfo(this);
        deviceInfo.setupGPS();

         TextView textView = findViewById(R.id.txtTest);
         textView.setText(deviceInfo.getLatitude());
        // deviceInfo.stopGPS();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //What is permission be request
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET,Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.BLUETOOTH,Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS}, 101);

    }

    //Check the permission is already have
    private boolean checkIfAlreadyhavePermission() {

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }


}
