package com.hitachi.com.klpod;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hitachi.com.klpod.Utility.DeviceInfo;
import com.hitachi.com.klpod.Utility.FuncDBAccess;
import com.hitachi.com.klpod.Utility.MasterAlert;
import com.hitachi.com.klpod.Utility.MasterServiceFunction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.Signature;

public class PlanDetailActivity extends AppCompatActivity{

    MasterServiceFunction masterServiceFunction = new MasterServiceFunction();
    MasterAlert masterAlert =new MasterAlert(this);
    DeviceInfo deviceInfo = new DeviceInfo(this);
    String DeliveryDetailNo;
    String ownerCode;
    String vehiclesCode;
    String StoreName,StoreCode;
    String DeliveryNo;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_plan_detail);

        DeliveryDetailNo = getIntent().getStringExtra("DeliveryDetailNo");
        vehiclesCode = getIntent().getStringExtra("VehiclesCode");
        ownerCode = getIntent().getStringExtra("OwnerCode");
        DeliveryNo = getIntent().getStringExtra("DeliveryNo");

        //create Toolbar
        createToolbar();

        //get Delivery Detail
        getDeliveryDetail();

        //arrived Button Click
        arrivedButtonClick();

        //signature Button Click
        signatureButtonClick();

        //return Button Click
        returnButtonClick();

        //confirm Button Click
        confirmButtonClick();
    }//main

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemExit) {

            finish();
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
        Toolbar toolbar = findViewById(R.id.toolbarPD);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_home);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ปุ่ม home คลิก
                openPlanListActivity();
            }
        });
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        openPlanListActivity();

    }

    private void  openPlanListActivity()
    {
        Intent intent = new Intent( PlanDetailActivity.this,PlanListActivity.class);
        intent.putExtra("VehiclesCode", vehiclesCode);
        startActivity(intent);
        finish();

    }

    private void confirmButtonClick() {
        Button confirmButton = findViewById(R.id.btnPDConfirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getUpdateDepartureTime()
                        +"/"+ DeliveryDetailNo
                        +"/"+ vehiclesCode
                );

                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if( Boolean.valueOf(jsonObject.getString("Result")))
                    {
                        setLayoutVisibility("false");
                        Toast.makeText(PlanDetailActivity.this, "Departed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PlanDetailActivity.this,PlanListActivity.class);
                        intent.putExtra("VehiclesCode",vehiclesCode);
                        startActivity(intent);

                        finish();
                    }
                    else
                    {
                        if(jsonObject.getString("MessageError").equals("NoReceiverName"))
                            masterAlert.normalDialog("Warning" ,"Please sign signature.");
                        else
                            Toast.makeText(PlanDetailActivity.this, "Cannot update data because :" + jsonObject.getString("MessageError"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void returnButtonClick() {
        Button returnButton = findViewById(R.id.btnPDReturn);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanDetailActivity.this,RetrunCartActivity.class);
                intent.putExtra("StoreName",StoreName);
                intent.putExtra("DeliveryNo",DeliveryNo);
                intent.putExtra("StoreCode",StoreCode);
                intent.putExtra("vehiclesCode",vehiclesCode);
                startActivity(intent);
            }
        });
    }

    private void signatureButtonClick() {
        Button signatureButton = findViewById(R.id.btnPDSignature);
        signatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanDetailActivity.this,SignatureActivity.class);
                intent.putExtra("DeliveryDetailNo",DeliveryDetailNo);
                intent.putExtra("vehiclesCode",vehiclesCode);
                startActivity(intent);
            }
        });
    }

    private void arrivedButtonClick() {
        try {
            Button arrivedButton = findViewById(R.id.btnPDArrived);
            arrivedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String Latitude = "-",Longitude = "-";
                    deviceInfo.getLocation();
                    Latitude = deviceInfo.getLatitude();
                    Longitude = deviceInfo.getLongitude();

                    JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getUpdateArrivalTime()
                            +"/"+ DeliveryDetailNo
                            +"/"+ Latitude
                            +"/"+ Longitude
                            +"/"+ vehiclesCode
                    );

                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if( Boolean.valueOf(jsonObject.getString("Result")))
                        {
                            setLayoutVisibility("false");
                            Toast.makeText(PlanDetailActivity.this, "Arrived", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(PlanDetailActivity.this, "Cannot update data because :" + jsonObject.getString("MessageError"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

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

    private  void setLayoutVisibility(String ArrivalCheck)
    {
        LinearLayout arrivedLinearLayout = findViewById(R.id.linPDBelow);
        RelativeLayout fullRelativeLayout = findViewById(R.id.linPDFull);

        if(Boolean.valueOf(ArrivalCheck))
        {
            arrivedLinearLayout.setVisibility(View.VISIBLE);
            fullRelativeLayout.setVisibility(View.INVISIBLE);
        }
        else
        {
            arrivedLinearLayout.setVisibility(View.INVISIBLE);
            fullRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getDeliveryDetail() {
        try {
            JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getGetDeliveryPlanDetail()
                    +"/"+ DeliveryDetailNo
                    +"/"+ ownerCode
                    );
            Log.d("KLTag", "Delivery ==> " + jsonArray);

            JSONObject jsonObject = jsonArray.getJSONObject(0);

            TextView planCodeTextView = findViewById(R.id.txtPDPlanCode);
            TextView storeCodeTextView = findViewById(R.id.txtPDStoreCode);
            TextView storeNameTextView = findViewById(R.id.txtPDStoreName);
            TextView outBoundDateTextView = findViewById(R.id.txtPDOutboundDate);
            TextView cartQTYTextView = findViewById(R.id.txtPDCartQTY);
            TextView palletQTYTextView = findViewById(R.id.txtPDPalletQTY);
            TextView metalQTYTextView = findViewById(R.id.txtPDMetalQTY);

            StoreName = jsonObject.getString("StoreName");
            StoreCode = jsonObject.getString("StoreCode");
            String planCode = planCodeTextView.getText() + (jsonObject.getString("TripNo").equals("null")? "" : jsonObject.getString("TripNo"));
            String storeCode = storeCodeTextView.getText() + (jsonObject.getString("StoreCode").equals("null")? "" : jsonObject.getString("StoreCode"));
            String storeName = storeNameTextView.getText() + (jsonObject.getString("StoreName").equals("null")? "" : jsonObject.getString("StoreName"));
            String outBoundDate = outBoundDateTextView.getText() + (jsonObject.getString("OutboundDate").equals("null")? "" : jsonObject.getString("OutboundDate"));
            String cartQTY = cartQTYTextView.getText() + (jsonObject.getString("CartQty").equals("null")? "" : jsonObject.getString("CartQty"));
            String palletQTY = palletQTYTextView.getText() + (jsonObject.getString("PalletQty").equals("null")? "" : jsonObject.getString("PalletQty"));
            String metalQTY = metalQTYTextView.getText() + (jsonObject.getString("MetalCartQty").equals("null")? "" : jsonObject.getString("MetalCartQty"));


            planCodeTextView.setText(planCode);
            storeCodeTextView.setText(storeCode);
            storeNameTextView.setText(storeName);
            outBoundDateTextView.setText(outBoundDate);
            cartQTYTextView.setText(cartQTY);
            palletQTYTextView.setText(palletQTY);
            metalQTYTextView.setText(metalQTY);

            setLayoutVisibility(jsonObject.getString("ArrivalCheck"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
