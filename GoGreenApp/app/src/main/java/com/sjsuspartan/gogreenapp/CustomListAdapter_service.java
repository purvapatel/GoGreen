package com.sjsuspartan.gogreenapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by purvapatel on 12/3/17.
 */


public class CustomListAdapter_service extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;

    public CustomListAdapter_service(Activity context, String[] itemname) {
        super(context, R.layout.mylist, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
    }

    public View getView(int position, View view, ViewGroup parent) {

        // set the custom layout for the ListView.
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.service_mylist, null,true);

        // map the title and id for ListView.
        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        //Button btn = (Button) rowView.findViewById(R.id.btn);

        // Set data to the ListView layout.
        txtTitle.setText(itemname[position]);
        return rowView;

    };
}