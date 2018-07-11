package com.hitachi.com.klpod.Utility;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hitachi.com.klpod.R;

public class PlanListAdapter extends BaseAdapter{

    private Context context;
    private String[] locationStrings;
    private String[] timeStrings;
    private String[] departureCheckStrings;

    public PlanListAdapter(Context context, String[] locationStrings, String[] timeStrings, String[] departureCheckStrings) {
        this.context = context;
        this.locationStrings = locationStrings;
        this.timeStrings = timeStrings;
        this.departureCheckStrings = departureCheckStrings;
    }

    @Override
    public int getCount() {
        return locationStrings.length;
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
        View view = layoutInflater.inflate(R.layout.listview_plan_list,parent,false);

        TextView locationTextView = view.findViewById(R.id.txtLPLLocation);
        TextView timeTextView = view.findViewById(R.id.txtLPLTime);

        locationTextView.setText(locationStrings[position]);
        timeTextView.setText(timeStrings[position]);
        if(Boolean.valueOf(departureCheckStrings[position])) {
            view.setBackgroundColor(Color.DKGRAY);
        }
        return view;
    }
}
