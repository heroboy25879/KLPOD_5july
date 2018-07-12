package com.hitachi.com.klpod;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hitachi.com.klpod.Utility.FuncDBAccess;
import com.hitachi.com.klpod.Utility.FuncUploadImage;
import com.hitachi.com.klpod.Utility.MasterAlert;
import com.hitachi.com.klpod.Utility.MasterServiceFunction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SignatureActivity extends Activity{


    MasterServiceFunction masterServiceFunction = new MasterServiceFunction();
    MasterAlert masterAlert = new MasterAlert(this);
    Button clearButton,saveButton;
    LinearLayout mContent;
    signature mSignature;
    View mView;
    private Bitmap mBitmap;
    String deliveryDetailNo,vehiclesCode,DeliveryNo;
    String nameImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        deliveryDetailNo = getIntent().getStringExtra("DeliveryDetailNo");
        vehiclesCode = getIntent().getStringExtra("vehiclesCode");
        DeliveryNo = getIntent().getStringExtra("DeliveryNo");

        saveButton = findViewById(R.id.btnSNSave);
        clearButton = findViewById(R.id.btnSNClear);
        mContent = findViewById(R.id.linSNSign);
        mSignature = new signature(this,null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        mView = mContent;


        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignature.clear();
            }
        });

        //save Button Click
        saveButtonClick();
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

    private void saveButtonClick() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText nameEditText = findViewById(R.id.edtSNName);
                if(!nameEditText.getText().toString().trim().equals("")) {
                    String resultUploadSign = mSignature.save(mView);
                    Log.v("KLTag", "resultUploadSign = > " + resultUploadSign);
                    if(Boolean.valueOf(resultUploadSign)) {
                        JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getUpdateReceiverName()
                                + "/" + deliveryDetailNo
                                + "/" + nameEditText.getText().toString().trim().replace(" ","@@")
                                + "/" + vehiclesCode
                        );

                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (Boolean.valueOf(jsonObject.getString("Result"))) {

                                //insert name of image
                                WebserviceExecute(masterServiceFunction.getInsertImage()
                                        +"/"+ deliveryDetailNo
                                        +"/"+ 5
                                        +"/"+ nameImage
                                        +"/"+ vehiclesCode);

                                Toast.makeText(SignatureActivity.this, "Signature save", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {

                                Toast.makeText(SignatureActivity.this, "Cannot save data because :" + jsonObject.getString("MessageError"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else
                        Toast.makeText(SignatureActivity.this, "Signature cannot upload.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    masterAlert.normalDialog("Warning","Please type receiver name.");

                }
            }
        });
    }


    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }


        public String save(View v) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);

                Canvas canvas = new Canvas(mBitmap);
                try {
//                    FileOutputStream mFileOutStream = new FileOutputStream(mypath);

                    v.draw(canvas);
//                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                    //   String url = Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);

                    Bitmap bitmap = mBitmap ;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] data2 = stream.toByteArray();
                    String encodedImage = Base64.encodeToString(data2, Base64.DEFAULT);

                    Log.v("KLTag", "Bitmap=++++++++++++++: " + encodedImage.length());

                    String dateStamp = new SimpleDateFormat("ddMMyyyy").format(Calendar.getInstance().getTime());
                    nameImage = DeliveryNo + "_5_" + dateStamp;

                    FuncUploadImage funcUploadImage = new FuncUploadImage(SignatureActivity.this,encodedImage,nameImage);
                    funcUploadImage.execute();
                    return  funcUploadImage.get();
                } catch (Exception e) {
                    Log.v("KLTag", e.toString());
                    return "";
                }
            }//end if
            else
                return "";
        }


        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            //mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

}
