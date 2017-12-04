package com.sjsuspartan.gogreenapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sjsuspartan.gogreenapp.Supporting_files.AppConfig;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class MyServiceFragment extends Fragment {

    ListView list;

    String[] serviceList = {
    };

    String[] serviceIdList = {
    };

    SharedPreferences cmpe235prefs;

    String BASE_URL = "https://gogreen-spartan-app.herokuapp.com/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View my_view = inflater.inflate(R.layout.fragment_find, container, false);

        cmpe235prefs = getActivity().getSharedPreferences("cmpe235", Context.MODE_PRIVATE);
        final String user_name = cmpe235prefs.getString("name","NA");


        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .setClient(new OkClient(new OkHttpClient()));

        RestAdapter adapter = builder.build();

        AppConfig.MyServices api = adapter.create(AppConfig.MyServices.class);

        api.get_my_services(
                user_name,
                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {

                        try {

                            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            String resp;
                            resp = reader.readLine();
                            Log.d("success", "" + resp);

                            JSONArray jObj = new JSONArray(resp);
                            ArrayList<String> arr_name = new ArrayList<String>();
                            ArrayList<String> arr_id = new ArrayList<String>();

                            for (int i = 0; i < jObj.length(); i++) {
                                try {

                                    JSONObject obj = (JSONObject) jObj.get(i);
                                    arr_name.add(obj.getString("service_name"));
                                    arr_id.add(obj.getString("service_id"));

                                } catch (JSONException e) {
                                    Toast.makeText(getActivity(), "Customer data not available", Toast.LENGTH_SHORT).show();
                                    Log.d("JsonException", e.toString());
                                }
                            }

                            serviceList = arr_name.toArray(new String[arr_name.size()]);
                            serviceIdList = arr_id.toArray(new String[arr_id.size()]);


                            // List adapter for list view
                            CustomListAdapter_service listadapter = new CustomListAdapter_service(getActivity(), serviceList);
                            //get the reference of the list view from activity_main
                            list = (ListView) my_view.findViewById(R.id.list);
                            //set adapterview with listview
                            list.setAdapter(listadapter);


                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {

                                    RestAdapter.Builder builder = new RestAdapter.Builder()
                                            .setEndpoint(BASE_URL) //Setting the Root URL
                                            .setClient(new OkClient(new OkHttpClient()));

                                    RestAdapter adapter = builder.build();

                                    AppConfig.ServiceDetail api = adapter.create(AppConfig.ServiceDetail.class);

                                    api.get_servicedetail_byid(
                                            serviceIdList[position],
                                            new Callback<Response>() {
                                                @Override
                                                public void success(Response result, Response response) {

                                                    try {

                                                        BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                                                        String resp;
                                                        resp = reader.readLine();
                                                        Log.d("success", "" + resp);

                                                        JSONArray jObj = new JSONArray(resp);
                                                        final JSONObject obj = (JSONObject) jObj.get(0);

                                                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());

                                                        //provide custom dialog view.
                                                        View mView = layoutInflaterAndroid.inflate(R.layout.buy_service_dialog_box, null);
                                                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());

                                                        // set view to the current context.
                                                        alertDialogBuilderUserInput.setView(mView);

                                                        final Button btn = (Button) mView.findViewById(R.id.btn_buy_service);
                                                        btn.setVisibility(View.GONE);
                                                        final EditText name = (EditText) mView.findViewById(R.id.service_name);
                                                        name.setEnabled(false);
                                                        final EditText location = (EditText) mView.findViewById(R.id.service_location);
                                                        location.setEnabled(false);
                                                        final EditText rate = (EditText) mView.findViewById(R.id.service_rate);
                                                        rate.setEnabled(false);
                                                        final EditText supplier = (EditText) mView.findViewById(R.id.service_supplier);
                                                        supplier.setEnabled(false);

                                                        name.setText(obj.getString("name"));
                                                        location.setText(obj.getString("location"));
                                                        rate.setText(obj.getString("rate"));
                                                        supplier.setText(obj.getString("supplier_name"));


                                                        // store feedback information into database
                                                        alertDialogBuilderUserInput
                                                                .setCancelable(false)
                                                                .setPositiveButton("", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialogBox, int id) {
                                                                        // ToDo get user input here

                                                                    }
                                                                })

                                                                .setNegativeButton("Cancel",
                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialogBox, int id) {
                                                                                dialogBox.cancel();
                                                                            }
                                                                        });

                                                        //create dialog box.
                                                        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();

                                                        //display dialog on current context.
                                                        alertDialogAndroid.show();




                                                    } catch (IOException e) {
                                                        Log.d("Exception", e.toString());
                                                    }catch (JSONException e) {
                                                        Log.d("JsonException", e.toString());
                                                    }
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    Toast.makeText(getActivity(), "Data not found", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                    );



                                }

                            });


                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Toast.makeText(getActivity().getApplicationContext(), "Customer data not available", Toast.LENGTH_SHORT).show();
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "Data not found", Toast.LENGTH_LONG).show();
                    }
                }
        );


        return my_view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("My Services");
    }

}
