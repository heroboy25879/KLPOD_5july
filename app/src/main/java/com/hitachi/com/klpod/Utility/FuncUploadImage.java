package com.hitachi.com.klpod.Utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class FuncUploadImage extends AsyncTask<String,Void,String>{

    private Context context;
    private String encodeImage, nameImage;

    public FuncUploadImage(Context context, String encodeImage, String nameImage) {
        this.context = context;
        this.encodeImage = encodeImage;
        this.nameImage = nameImage;
    }

    MasterServiceFunction masterServiceFunction = new MasterServiceFunction();
    private static String NAMESPACE = "http://tempuri.org/"; // namespace of the
    // webservice
    private static String URL = "ImageService.asmx";

    private static String SOAP_ACTION = "http://tempuri.org/";




    String serverreponse = " ";
    SoapSerializationEnvelope envelope;
    SoapPrimitive response;
    SoapObject request;
    PropertyInfo ImgName, ImgValue;
    HttpTransportSE androidHttpTransport;

    private ProgressDialog progressDialog;

    public String getServerreponse() {
        return serverreponse;
    }

    @Override
    protected String doInBackground(String... strings) {


        request = new SoapObject(NAMESPACE, "Upload");
        // creating the SoapObject and "Hello" is the method name of the webservice which will called.
        ImgName = new PropertyInfo();
        ImgName.setName("ImgName"); // setting the variable name
        ImgName.setValue(nameImage);// setting the value
        ImgName.setType(String.class); // setting the type
        request.addProperty(ImgName);// adding the property

        ImgValue = new PropertyInfo();
        ImgValue.setName("ImgValue");
        ImgValue.setValue(encodeImage);
        ImgValue.setType(String.class);
        request.addProperty(ImgValue);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        // creating a serialized envelope which will be used to carry the
        // parameters for SOAP body and call the method
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        androidHttpTransport = new HttpTransportSE(masterServiceFunction.getUrlService() + URL);

        try {

            androidHttpTransport.call(SOAP_ACTION + "Upload", envelope); // Hello
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
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Photo process");
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
}
