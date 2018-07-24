package com.hitachi.com.klpod.Utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class FuncDBAccess extends AsyncTask<String,Void,String>{

    private Context context;
    private ProgressDialog progressDialog;
    public FuncDBAccess(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            OkHttpClient okHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder();

            Request request = builder
                    .url(strings[0])
                    .build();



            Response response = okHttpClient.newCall(request).execute();
            Log.d("KLTag","response ==> " + response);

            //กรณีเจอ bad request
            if(response.toString().contains("code=400"))
            {
                FuncRefreshService funcRefreshService = new FuncRefreshService(context);
                funcRefreshService.execute();

                //excute อีกครั้ง
                response = okHttpClient.newCall(request).execute();
            }

            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("System process");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {

        progressDialog.dismiss();
    }
    public String SetJSONResult(String resultJSON)
    {
        resultJSON = resultJSON.replace("\\\"","\"");
        return resultJSON.substring(1,resultJSON.length()-1);

    }
}
