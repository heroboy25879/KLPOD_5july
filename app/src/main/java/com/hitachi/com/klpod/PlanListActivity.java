package com.hitachi.com.klpod;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hitachi.com.klpod.Fragment.LoginFragment;
import com.hitachi.com.klpod.Utility.DeviceInfo;
import com.hitachi.com.klpod.Utility.FuncDBAccess;
import com.hitachi.com.klpod.Utility.MasterAlert;
import com.hitachi.com.klpod.Utility.MasterServiceFunction;
import com.hitachi.com.klpod.Utility.PlanListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlanListActivity extends AppCompatActivity{


    MasterServiceFunction masterServiceFunction = new MasterServiceFunction();
    MasterAlert masterAlert =new MasterAlert(this);
    DeviceInfo deviceInfo =new DeviceInfo(this);
    String vehiclesCode ;
    String outBoundDate;
    String DeliveryNo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);

        vehiclesCode = getIntent().getStringExtra("VehiclesCode");
        DeliveryNo = "-";
        //create Toolbar
        createToolbar();

        //Get Outbound Date
        getOutboundDate();
        //Get Plan Detail
        getDelivery();
        //Check Button Start Job
        checkButtonStartJob();
        //Check Button End Job
        checkButtonEndJob();
        //Start Button Click
        startButtonClick();
        //end Button Click
        endButtonClick();


    } // main method



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemExit) {

            JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getUpdateUserlogout()
                    +"/"+ deviceInfo.IMEI());

            if(jsonArray.length() > 0) {
                Toast.makeText(PlanListActivity.this,"Logout.",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PlanListActivity.this, MainActivity.class));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plan_list,menu);
        return true;
    }

    private void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarPL);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_home);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ปุ่ม home คลิก
            }
        });

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void endButtonClick() {
        Button endButton = findViewById(R.id.btnPLEndJob);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PlanListActivity.this);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_action_alert);
                builder.setTitle("Confirm end job");
                builder.setMessage("Do you want end job?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //end Job
                        endJob();
                        dialog.dismiss();
                    }
                });
                builder.show();




            }// onclick
        });
    }

    private void endJob() {
        String LatitudeArrival = "-",LongitudeArrival = "-";
        DeviceInfo deviceInfo = new DeviceInfo(this);
        deviceInfo.setupGPS();
        LatitudeArrival = deviceInfo.getLatitude();
        LongitudeArrival = deviceInfo.getLongitude();
        JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getUpdateEndJob()
                +"/"+ DeliveryNo
                +"/"+ LatitudeArrival
                +"/"+ LongitudeArrival
                +"/"+ vehiclesCode
        );
        Log.d("KLTag", "printStackTrace ==> " + jsonArray);
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            if( Boolean.valueOf(jsonObject.getString("Result")))
            {
                Toast.makeText(PlanListActivity.this, "Job Ended", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,PlanListActivity.class);
                intent.putExtra("VehiclesCode", vehiclesCode);
                startActivity(intent);
                //finish();
            }
            else
            {
                Toast.makeText(PlanListActivity.this, "Job cannot end because :" + jsonObject.getString("MessageError"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("KLTag", "printStackTrace ==> " + e.getMessage());
        }
    }

    private void startButtonClick() {
        final Button StartJobButton = findViewById(R.id.btnPLStartJob);
        StartJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PlanListActivity.this);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_action_alert);
                builder.setTitle("Confirm start job");
                builder.setMessage("Do you want start job?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //start Job
                        startJob(StartJobButton);
                        dialog.dismiss();
                    }
                });
                builder.show();




            }// on click
        });
    }

    private void startJob(Button startJobButton) {
        String LatitudeDeparture = "-",LongitudeDeparture = "-";
        DeviceInfo deviceInfo = new DeviceInfo(this);
        deviceInfo.setupGPS();
        LatitudeDeparture = deviceInfo.getLatitude();
        LongitudeDeparture = deviceInfo.getLongitude();
        JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getUpdateStartJob()
                +"/"+ DeliveryNo
                +"/"+ LatitudeDeparture
                +"/"+ LongitudeDeparture
                +"/"+ deviceInfo.IMEI()
                +"/"+ vehiclesCode
        );

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            if( Boolean.valueOf(jsonObject.getString("Result")))
            {
                startJobButton.setVisibility(View.GONE);
                Toast.makeText(PlanListActivity.this, "Job Started", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(PlanListActivity.this, "Job cannot start because :" + jsonObject.getString("MessageError"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkButtonEndJob() {
        try {
            Button EndJobButton = findViewById(R.id.btnPLEndJob);
            JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getCheckEndJob()
                    +"/"+ DeliveryNo);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            if( Boolean.valueOf(jsonObject.getString("Result")))
                EndJobButton.setVisibility(View.VISIBLE);
            else
                EndJobButton.setVisibility(View.GONE);

            Log.d("KLTag", "EndJobButton getVisibility ==> " + EndJobButton.getVisibility());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONArray WebserviceExecute(String UrlFunc)
    {
        try {
            FuncDBAccess funcDBAccess = new FuncDBAccess(this);
            funcDBAccess.execute(UrlFunc);
            String resultJSON = funcDBAccess.SetJSONResult(funcDBAccess.get());
            JSONArray jsonArray = new JSONArray(resultJSON);

            return  jsonArray;
        } catch (Exception e)
        {
            e.printStackTrace();
            return  null;
        }

    }

    private void checkButtonStartJob() {
        try {
            Button StartJobButton = findViewById(R.id.btnPLStartJob);
            JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getCheckStartJob()
                    +"/"+ DeliveryNo);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            if( Boolean.valueOf(jsonObject.getString("Result")))
                StartJobButton.setVisibility(View.VISIBLE);
            else
                StartJobButton.setVisibility(View.GONE);

            Log.d("KLTag", "StartJobButton getVisibility ==> " + StartJobButton.getVisibility());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createPlanList(JSONArray jsonArray) {
        ListView listView = findViewById(R.id.lvPLPlanList);
        final String[] locationStrings = new String[jsonArray.length()];
        final String[] timeStrings = new String[jsonArray.length()];
        final String[] DeliveryDetailNoStrings = new String[jsonArray.length()];
        final String[] ownerCodeStrings = new String[jsonArray.length()];
        final String[] departureCheckStrings = new String[jsonArray.length()];

        try {
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                locationStrings[i] = jsonObject.getString("StoreName");
                timeStrings[i] = jsonObject.getString("EstimateArrivalTime");
                DeliveryDetailNoStrings[i] = jsonObject.getString("DeliveryDetailNo");
                ownerCodeStrings[i] = jsonObject.getString("OwnerCode");
                departureCheckStrings[i] = jsonObject.getString("DepartureCheck");
            }
            Log.d("KLTag", "Location ==> " + locationStrings[0]);
            PlanListAdapter planListAdapter =new PlanListAdapter(this,locationStrings,timeStrings,departureCheckStrings);
            listView.setAdapter(planListAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //listView Item Click
                    Button StartJobButton = findViewById(R.id.btnPLStartJob);
                    if(!Boolean.valueOf(departureCheckStrings[position])) {
                        if (StartJobButton.getVisibility() != View.VISIBLE) {
                            Intent intent = new Intent(PlanListActivity.this, PlanDetailActivity.class);
                            intent.putExtra("DeliveryDetailNo", DeliveryDetailNoStrings[position]);
                            intent.putExtra("OwnerCode", ownerCodeStrings[position]);
                            intent.putExtra("VehiclesCode", vehiclesCode);
                            intent.putExtra("DeliveryNo", DeliveryNo);
                            startActivity(intent);
                            finish();
                        } else {
                            masterAlert.normalDialog("Warning", "Please press Start Job button.");
                        }
                    }
                    else
                    {
                        Toast.makeText(PlanListActivity.this, "This store has been shipped.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getDelivery() {
        try {
            JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getGetDeliveryPlan()
                    +"/"+ vehiclesCode
                    +"/"+ outBoundDate);
            Log.d("KLTag", "Delivery ==> " + jsonArray);

            if(jsonArray != null && jsonArray.length() > 0 ) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                TextView driverNameTextView = findViewById(R.id.txtPLDriverName);
                TextView carLicenseTextView = findViewById(R.id.txtPLCarLicense);
                TextView planCodeTextView = findViewById(R.id.txtPLPlanCode);

                String driverName = " " + (jsonObject.getString("DriverName").equals("null") ? "" : jsonObject.getString("DriverName"));
                String carLicense = " " + (jsonObject.getString("VehiclesName").equals("null") ? "" : jsonObject.getString("VehiclesName"));
                String planCode =  " " +(jsonObject.getString("TripNo").equals("null") ? "" : jsonObject.getString("TripNo"));
                DeliveryNo = jsonObject.getString("DeliveryNo");

                driverNameTextView.setText(driverName);
                carLicenseTextView.setText(carLicense);
                planCodeTextView.setText(planCode);

                //Create Plan List
                createPlanList(jsonArray);
            }
            else
            {
                Log.d("KLTag", "Warning ==> " );
                Toast.makeText(this,"No plan for this truck!!",Toast.LENGTH_SHORT).show();
                //masterAlert.normalDialog("Warning","No plan for this truck!!");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getOutboundDate() {
        try {
            JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getGetOutboundDate()
                    +"/"+ vehiclesCode);
            Log.d("KLTag", "OutboundDate ==> " + jsonArray);

            String[] outBoundDateStrings = new String[jsonArray.length()];
            final String[] OutboundDateFullFormatStrings = new String[jsonArray.length()];
            for (int i = 0;i<jsonArray.length() ; i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                outBoundDateStrings[i] =  jsonObject.getString("OutboundDate");
                OutboundDateFullFormatStrings[i] = jsonObject.getString("OutboundDateFullFormat");

            }
            outBoundDate = OutboundDateFullFormatStrings[0];

            Spinner spinner = findViewById(R.id.spinerPLOutboundDate);
            ArrayAdapter<String>  stringArrayAdapter = new ArrayAdapter<String>(PlanListActivity.this,
                    android.R.layout.simple_list_item_1,
                    outBoundDateStrings);
            spinner.setAdapter(stringArrayAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    outBoundDate = OutboundDateFullFormatStrings[position];

                    // ทำต่อด้วย
                    getDelivery();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    outBoundDate = OutboundDateFullFormatStrings[0];
                }
            });

//            Button outBoundDateButton = findViewById(R.id.btnPLOutboundDate);
////            outBoundDateButton.setText(jsonObject.getString("OutboundDate"));
     //       outBoundDate = OutboundDateFullFormatStrings[0];

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}

