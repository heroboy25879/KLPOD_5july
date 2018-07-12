package com.hitachi.com.klpod;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hitachi.com.klpod.Utility.FuncDBAccess;
import com.hitachi.com.klpod.Utility.MasterAlert;
import com.hitachi.com.klpod.Utility.MasterServiceFunction;
import com.hitachi.com.klpod.Utility.PlanListAdapter;
import com.hitachi.com.klpod.Utility.ReturnCartAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

public class RetrunCartActivity  extends Activity{

    MasterServiceFunction masterServiceFunction = new MasterServiceFunction();
    MasterAlert masterAlert =new MasterAlert(this);
    String[] containerCodeStrings;
    String[] nameStrings;
    String storeName,StoreCode;
    String DeliveryNo;
    String vehiclesCode;
    String DeliveryDetailNo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_return_cart);
        storeName = getIntent().getStringExtra("StoreName");
        StoreCode = getIntent().getStringExtra("StoreCode");
        DeliveryNo = getIntent().getStringExtra("DeliveryNo");
        vehiclesCode = getIntent().getStringExtra("vehiclesCode");
        DeliveryDetailNo = getIntent().getStringExtra("DeliveryDetailNo");
        TextView storeNameTextView = findViewById(R.id.txtRTStoreName);
        storeNameTextView.setText(storeNameTextView.getText() +  storeName);


        //get Container List
        getContainerList();

        //finish Button Click
        finishButtonClick();
    }

    private void finishButtonClick() {
        Button finishButton = findViewById(R.id.btnRTFinish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = "";
                ListView listView = findViewById(R.id.lvRCContainer);
                for(int i=0;i<listView.getCount();i++) {
                    View view = listView.getChildAt(i);
                    EditText editText = view.findViewById(R.id.edtRTQty);
                    String ContainerQty = editText.getText().toString().trim();

                    
                        JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getInsertTrDeliveryReturn()
                                + "/" + DeliveryDetailNo
                                + "/" + StoreCode
                                + "/" + containerCodeStrings[i]
                                + "/" + ContainerQty
                                + "/" + vehiclesCode
                        );

                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (Boolean.valueOf(jsonObject.getString("Result"))) {
                                result += nameStrings[i] + "completed\n" ;
                            } else {
                                result += nameStrings[i] + "Incomplete\n" ;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                }
                Toast.makeText(RetrunCartActivity.this, "Return result :\n" + result, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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

    private void getContainerList() {
        try {
            JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getGetContainer()
                            +"/" + DeliveryDetailNo
                            +"/" + StoreCode
                        );

            ListView listView = findViewById(R.id.lvRCContainer);
            nameStrings = new String[jsonArray.length()];
            final String[] desStrings = new String[jsonArray.length()];
            final String[] containerQtyStrings = new String[jsonArray.length()];
            containerCodeStrings = new String[jsonArray.length()];


                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    nameStrings[i] = jsonObject.getString("ContainerName");
                    desStrings[i] = jsonObject.getString("Description");
                    containerCodeStrings[i] = jsonObject.getString("ContainerCode");
                    containerQtyStrings[i] = jsonObject.getString("ContainerQty");
                }
                Log.d("KLTag", "ContainerList ==> " + jsonArray);

            ReturnCartAdapter returnCartAdapter = new ReturnCartAdapter(this,nameStrings,desStrings,containerQtyStrings);
            listView.setAdapter(returnCartAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
