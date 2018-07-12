package com.hitachi.com.klpod;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hitachi.com.klpod.Utility.DeviceInfo;
import com.hitachi.com.klpod.Utility.FuncDBAccess;
import com.hitachi.com.klpod.Utility.FuncUploadImage;
import com.hitachi.com.klpod.Utility.MasterAlert;
import com.hitachi.com.klpod.Utility.MasterServiceFunction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PlanDetailActivity extends AppCompatActivity{

    MasterServiceFunction masterServiceFunction = new MasterServiceFunction();
    MasterAlert masterAlert =new MasterAlert(this);
    DeviceInfo deviceInfo = new DeviceInfo(this);
    String DeliveryDetailNo;
    String ownerCode;
    String vehiclesCode;
    String StoreName,StoreCode;
    String DeliveryNo;
    String[] imageName = new String[4];
    ImageButton camera1ImageButton,camera2ImageButton,camera3ImageButton,camera4ImageButton;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_plan_detail);

        DeliveryDetailNo = getIntent().getStringExtra("DeliveryDetailNo");
        vehiclesCode = getIntent().getStringExtra("VehiclesCode");
        ownerCode = getIntent().getStringExtra("OwnerCode");
        DeliveryNo = getIntent().getStringExtra("DeliveryNo");
        imageName[0] = "";
        imageName[1] = "";
        imageName[2] = "";
        imageName[3] = "";
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

        //camera 1 Click
        camera1Click();
        //camera 2 Click
        camera2Click();
        //camera 3 Click
        camera3Click();
        //camera 4 Click
        camera4Click();
    }//main

    private void camera4Click() {
        camera4ImageButton = findViewById(R.id.btnPDCamera4);
        camera4ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 456);
            }
        });
    }

    private void camera3Click() {
        camera3ImageButton = findViewById(R.id.btnPDCamera3);
        camera3ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 345);
            }
        });
    }

    private void camera2Click() {
        camera2ImageButton = findViewById(R.id.btnPDCamera2);
        camera2ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 234);
            }
        });
    }

    private void camera1Click() {
        camera1ImageButton = findViewById(R.id.btnPDCamera1);
        camera1ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 123);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String dateStamp = new SimpleDateFormat("ddMMyyyy").format(Calendar.getInstance().getTime());
            String nameImage = "";
            Uri selectedImage = data.getData();
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            switch (requestCode)
            {
                case 123 : camera1ImageButton.setImageBitmap(bitmap);
                    nameImage = DeliveryNo + "_1_" + dateStamp;
                    break;
                case 234 : camera2ImageButton.setImageBitmap(bitmap);
                    nameImage = DeliveryNo + "_2_" + dateStamp;
                    break;
                case 345 : camera3ImageButton.setImageBitmap(bitmap);
                    nameImage = DeliveryNo + "_3_" + dateStamp;
                    break;
                case 456 : camera4ImageButton.setImageBitmap(bitmap);
                    nameImage = DeliveryNo + "_4_" + dateStamp;
                    break;

            }

            Bitmap originBitmap = null;

            InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(
                        selectedImage);
                originBitmap = BitmapFactory.decodeStream(imageStream);

            } catch (FileNotFoundException e) {
            }

            //Bitmap originBitmap2 = Bitmap.createScaledBitmap(originBitmap,(int)(originBitmap.getWidth()*0.9), (int)(originBitmap.getHeight()*0.9), true);
            if (originBitmap.getHeight() < originBitmap.getWidth()) {
                originBitmap = rotateBitmap(originBitmap);
            }
            Bitmap originBitmap2 = Bitmap.createScaledBitmap(originBitmap,489, 652, true);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            originBitmap2.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byte[] data2 = stream.toByteArray();
            String encodedImage = Base64.encodeToString(data2, Base64.DEFAULT);

            try {
                FuncUploadImage funcUploadImage = new FuncUploadImage(PlanDetailActivity.this,encodedImage,nameImage);
                funcUploadImage.execute();
                String serverreponse = funcUploadImage.get();
                if(serverreponse.equals("true"))
                {
                    switch (requestCode)
                    {
                        case 123 : imageName[0] = nameImage;
                            break;
                        case 234 : imageName[1] = nameImage;
                            break;
                        case 345 : imageName[2] = nameImage;
                            break;
                        case 456 : imageName[3] = nameImage;
                            break;
                    }
                }
                else
                {
                    Toast.makeText(PlanDetailActivity.this,"Photo save fail.",Toast.LENGTH_SHORT).show();
                    switch (requestCode)
                    {
                        case 123 : camera1ImageButton.setImageResource(R.drawable.camera);
                            break;
                        case 234 : camera2ImageButton.setImageResource(R.drawable.camera);
                            break;
                        case 345 : camera3ImageButton.setImageResource(R.drawable.camera);
                            break;
                        case 456 : camera4ImageButton.setImageResource(R.drawable.camera);
                            break;
                    }
                }
            } catch (Exception e) {
            }


        }
    }

    private Bitmap rotateBitmap(Bitmap src) {

        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(90);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemExit) {

            JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getUpdateUserlogout()
                    +"/"+ deviceInfo.IMEI());

            if(jsonArray.length() > 0) {
                Toast.makeText(PlanDetailActivity.this,"Logout.",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PlanDetailActivity.this, MainActivity.class));
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

                AlertDialog.Builder builder = new AlertDialog.Builder(PlanDetailActivity.this);
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_action_alert);
                builder.setTitle("Confirm job");
                builder.setMessage("Do you want confirm job and departure?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //confirm
                        confirm();
                        dialog.dismiss();
                    }
                });
                builder.show();


            }
        });
    }

    private void confirm() {
        JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getUpdateDepartureTime()
                +"/"+ DeliveryDetailNo
                +"/"+ vehiclesCode
        );

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            if( Boolean.valueOf(jsonObject.getString("Result")))
            {
                for (int i = 0 ; i <4 ; i++)
                {
                    //insert name of image
                    WebserviceExecute(masterServiceFunction.getInsertImage()
                            +"/"+ DeliveryDetailNo
                            +"/"+ (i+1)
                            +"/"+ imageName[i]
                            +"/"+ vehiclesCode);

                }
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
                intent.putExtra("DeliveryDetailNo",DeliveryDetailNo);
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
                intent.putExtra("DeliveryNo",DeliveryNo);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlanDetailActivity.this);
                    builder.setCancelable(false);
                    builder.setIcon(R.drawable.ic_action_alert);
                    builder.setTitle("Arrived");
                    builder.setMessage("Are you arrived?");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //arrived
                            arrived();
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void arrived() {
        String Latitude = "-",Longitude = "-";
        DeviceInfo deviceInfo = new DeviceInfo(this);
        deviceInfo.setupGPS();
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
            String planCode = " " + (jsonObject.getString("TripNo").equals("null")? "" : jsonObject.getString("TripNo"));
            String storeCode = " " + (jsonObject.getString("StoreCode").equals("null")? "" : jsonObject.getString("StoreCode"));
            String storeName = " " + (jsonObject.getString("StoreName").equals("null")? "" : jsonObject.getString("StoreName"));
            String outBoundDate = " " + (jsonObject.getString("OutboundDate").equals("null")? "" : jsonObject.getString("OutboundDate"));
            String cartQTY = " " + (jsonObject.getString("CartQty").equals("null")? "" : jsonObject.getString("CartQty"));
            String palletQTY = " " + (jsonObject.getString("PalletQty").equals("null")? "" : jsonObject.getString("PalletQty"));
            String metalQTY = " " + (jsonObject.getString("MetalCartQty").equals("null")? "" : jsonObject.getString("MetalCartQty"));


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
