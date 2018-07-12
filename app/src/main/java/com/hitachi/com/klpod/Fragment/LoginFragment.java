package com.hitachi.com.klpod.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hitachi.com.klpod.PlanListActivity;
import com.hitachi.com.klpod.R;
import com.hitachi.com.klpod.Utility.DeviceInfo;
import com.hitachi.com.klpod.Utility.FuncDBAccess;
import com.hitachi.com.klpod.Utility.MasterAlert;
import com.hitachi.com.klpod.Utility.MasterServiceFunction;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginFragment extends Fragment {


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EditText userEditText = getView().findViewById(R.id.edtFLogin_User);
        EditText passEditText = getView().findViewById(R.id.edtFLogin_Password);
        userEditText.setText("BKT1585");
        passEditText.setText("1234");
        //Login Check
        LoginCheck();


    }

    private void LoginCheck() {

        Button button = getView().findViewById(R.id.btnFLogin_Login);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText userEditText = getView().findViewById(R.id.edtFLogin_User);
                EditText passEditText = getView().findViewById(R.id.edtFLogin_Password);

                String userString = userEditText.getText().toString().trim();
                String passString = passEditText.getText().toString().trim();

                MasterAlert masterAlert = new MasterAlert(getActivity());
                if (userString.isEmpty() || passString.isEmpty()) {
                    masterAlert.normalDialog("Warning!", "Please fill all data!");
                } else {
                    try {
                        MasterServiceFunction masterServiceFunction = new MasterServiceFunction();
                        DeviceInfo deviceInfo = new DeviceInfo(getContext());

                        FuncDBAccess funcDBAccess = new FuncDBAccess(getActivity());
                        funcDBAccess.execute(masterServiceFunction.getGetUserLogin()
                                +"/"+ userString
                                +"/"+ passString
                                +"/"+ deviceInfo.IMEI());

                        Log.d("KLTag","IMEI ==> " + deviceInfo.IMEI());


                        String resultJSON = funcDBAccess.SetJSONResult(funcDBAccess.get());

                        Log.d("KLTag","JSON ==> " + resultJSON);
                        JSONArray jsonArray = new JSONArray(resultJSON);

                        String resultLogin = "" ;
                        String vehiclesCode = "" ;

                        if (jsonArray.length() > 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            resultLogin = jsonObject.getString("Result");
                            vehiclesCode = jsonObject.getString("VehiclesCode");
                            Log.d("KLTag", "JSON ==> " + vehiclesCode);

                        } else {
                            masterAlert.normalDialog("Warning!", "Cannot Login! Please try again.");
                        }

                        switch (resultLogin) {
                            case "Valid" : Toast.makeText(getActivity(), "Login By : " + vehiclesCode, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(),PlanListActivity.class);
                                intent.putExtra("VehiclesCode", vehiclesCode);
                                startActivity(intent);
                                getActivity().finish();
                                break;
                            case  "Duplicate" :  masterAlert.normalDialog("Warning!", "Duplicate login! Please logout");
                                break;
                            case  "Invalid" : masterAlert.normalDialog("Warning!", "Invalid Username or Password");
                                break;
                            case "NORegister" : masterAlert.normalDialog("Warning!", "This mobile not register yet.");
                                break;
                            default: masterAlert.normalDialog("Warning!", "Cannot Login! Please try again.");
                                break;

                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }


}