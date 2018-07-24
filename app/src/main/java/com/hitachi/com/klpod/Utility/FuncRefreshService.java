package com.hitachi.com.klpod.Utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class FuncRefreshService extends AsyncTask<String,Void,String> {


    private Context context;

    public FuncRefreshService(Context context) {
        this.context = context;
    }

    MasterServiceFunction masterServiceFunction = new MasterServiceFunction();
    private static String NAMESPACE = "http://tempuri.org/"; // namespace of the
    // webservice
    private static String URL = "Refresh.asmx";

    private static String SOAP_ACTION = "http://tempuri.org/";




    String serverreponse = " ";
    SoapSerializationEnvelope envelope;
    SoapPrimitive response;
    SoapObject request;
    HttpTransportSE androidHttpTransport;



    @Override
    protected String doInBackground(String... strings) {


        request = new SoapObject(NAMESPACE, "MAS_POD_RefreshService");
        // creating the SoapObject and "Hello" is the method name of the webservice which will called.


        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        // creating a serialized envelope which will be used to carry the
        // parameters for SOAP body and call the method
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        androidHttpTransport = new HttpTransportSE(masterServiceFunction.getUrlRefreshService() + URL);

        try {

            androidHttpTransport.call(SOAP_ACTION + "RefreshService", envelope); // Hello
            response = (SoapPrimitive) envelope.getResponse();
            serverreponse = response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            serverreponse = "Error Occurred " + e.getCause() + e.getMessage();
        }

        Log.d("KLTag","serverreponse ==> " + serverreponse);

        return serverreponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {

    }
}
