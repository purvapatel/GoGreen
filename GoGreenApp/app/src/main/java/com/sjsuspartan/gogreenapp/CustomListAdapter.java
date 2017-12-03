package com.sjsuspartan.gogreenapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by purvapatel on 9/4/17.
 */

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;

    public CustomListAdapter(Activity context, String[] itemname) {
        super(context, R.layout.mylist, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
    }

    public View getView(int position, View view, ViewGroup parent) {

        // set the custom layout for the ListView.
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        // map the title and id for ListView.
        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);

        // Set data to the ListView layout.
        txtTitle.setText(itemname[position]);
        return rowView;

    };
}

