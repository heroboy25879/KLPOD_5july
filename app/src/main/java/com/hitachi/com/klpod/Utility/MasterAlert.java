package com.hitachi.com.klpod.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.hitachi.com.klpod.R;

public class MasterAlert {

    private Context context;

    public MasterAlert(Context context) {
        this.context = context;
    }

    public void  normalDialog(String titleString , String MessageString)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_action_alert);
        builder.setTitle(titleString);
        builder.setMessage(MessageString);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

}
