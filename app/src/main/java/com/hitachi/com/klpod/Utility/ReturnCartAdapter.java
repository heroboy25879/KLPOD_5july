package com.hitachi.com.klpod.Utility;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.hitachi.com.klpod.R;

public class ReturnCartAdapter extends BaseAdapter{

    private Context context;
    private String[] ContainerName;
    private String[] Description;
    private String[] containerQty;

    public ReturnCartAdapter(Context context, String[] containerName, String[] description, String[] containerQty) {
        this.context = context;
        ContainerName = containerName;
        Description = description;
        this.containerQty = containerQty;
    }

    @Override
    public int getCount() {
        return ContainerName.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.listview_return_cart,parent,false);





            TextView nameTextView = view.findViewById(R.id.txtRTName);
            TextView desTextView = view.findViewById(R.id.txtRTDes);
            EditText qtyEditText = view.findViewById(R.id.edtRTQty);

            nameTextView.setText(ContainerName[position]);
            desTextView.setText(Description[position]);
            qtyEditText.setText(containerQty[position]);

        qtyEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText qtyEditText = v.findViewById(R.id.edtRTQty);
                if (hasFocus) {

                    qtyEditText.setText("");
                } else {
                    Log.d("KLTag", "test ==> " + qtyEditText.getText());
                    if(qtyEditText.getText().toString().trim().equals(""))
                        qtyEditText.setText("0");



                }
            }
        });


        if (convertView != null)
            return convertView;



        return view;

    }
}
