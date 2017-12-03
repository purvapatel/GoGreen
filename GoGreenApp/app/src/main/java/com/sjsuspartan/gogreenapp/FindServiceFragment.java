package com.sjsuspartan.gogreenapp;

import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class FindServiceFragment extends Fragment {

    ListView list;

    String[] serviceList = {
    };

    String[] serviceIdList = {
    };

    String BASE_URL = "https://gogreen-spartan-app.herokuapp.com/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View my_view = inflater.inflate(R.layout.fragment_find, container, false);


        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .setClient(new OkClient(new OkHttpClient()));

        RestAdapter adapter = builder.build();

        AppConfig.ServiceList api = adapter.create(AppConfig.ServiceList.class);

        api.get_all_services(
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
                                    arr_name.add(obj.getString("name"));
                                    arr_id.add(obj.getString("_id"));

                                } catch (JSONException e) {
                                    Toast.makeText(getActivity(), "Customer data not available", Toast.LENGTH_SHORT).show();
                                    Log.d("JsonException", e.toString());
                                }
                            }

                            serviceList = arr_name.toArray(new String[arr_name.size()]);
                            serviceIdList = arr_id.toArray(new String[arr_id.size()]);


                            // List adapter for list view
                            CustomListAdapter listadapter = new CustomListAdapter(getActivity(), serviceList);
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
                                                        final String buy_supp_id =  obj.getString("supplier_id").toString();
                                                        final String buy_service_id = obj.getString("_id").toString();

                                                        btn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                                RestAdapter.Builder builder = new RestAdapter.Builder()
                                                                        .setEndpoint(BASE_URL) //Setting the Root URL
                                                                        .setClient(new OkClient(new OkHttpClient()));

                                                                // get the reference of the adapter
                                                                RestAdapter adapter = builder.build();

                                                                //get reference of adduserprofile interface
                                                                AppConfig.BuyService api = adapter.create(AppConfig.BuyService.class);

                                                                // create hashmap for the user info like name, mobile number, email id
                                                                // to store into database
                                                                HashMap<String, Object> map = new HashMap<String, Object>();
                                                                map.put("supplier_id", buy_supp_id);
                                                                map.put("supplier_name",supplier.getText().toString());
                                                                map.put("service_id", buy_service_id);
                                                                map.put("service_name", name.getText().toString());

                                                                // call method to insert user registration info
                                                                // pass map and callback object as a parameter
                                                                api.add_user_service(
                                                                        map,
                                                                        new Callback<Response>() {

                                                                            // if api called successfully
                                                                            @Override
                                                                            public void success(Response result, Response response) {

                                                                                try {

                                                                                    // to get response in form of json
                                                                                    BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                                                                                    String resp;
                                                                                    resp = reader.readLine();
                                                                                    Log.d("success", "" + resp);

                                                                                    JSONObject jObj = new JSONObject(resp);
                                                                                    int success = jObj.getInt("success");


                                                                                    if(success == 1){
                                                                                        // if data stored successfully
                                                                                        //Toast.makeText(getApplicationContext(), "Added successfully", Toast.LENGTH_SHORT).show();


                                                                                    } else{
                                                                                        // if failure happened during insertion of data
                                                                                        Toast.makeText(getActivity().getApplicationContext(), "Unable to perform buy service operation", Toast.LENGTH_SHORT).show();
                                                                                    }


                                                                                } catch (IOException e) {
                                                                                    Log.d("Exception", e.toString());
                                                                                } catch (JSONException e) {
                                                                                    Log.d("JsonException", e.toString());
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void failure(RetrofitError error) {
                                                                                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                );
                                                            }
                                                        });

                                                        final String service_id = obj.getString("_id");

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

        getActivity().setTitle("Supplier");
    }

    public void buyService(View view, String supplier_name, String supplier_id, String service_name, String service_id)
    {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .setClient(new OkClient(new OkHttpClient()));

        // get the reference of the adapter
        RestAdapter adapter = builder.build();

        //get reference of adduserprofile interface
        AppConfig.BuyService api = adapter.create(AppConfig.BuyService.class);

        // create hashmap for the user info like name, mobile number, email id
        // to store into database
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("supplier_id", supplier_id);
        map.put("supplier_name",supplier_name);
        map.put("service_id", service_id);
        map.put("service_name", service_name);

        // call method to insert user registration info
        // pass map and callback object as a parameter
        api.add_user_service(
                map,
                new Callback<Response>() {

                    // if api called successfully
                    @Override
                    public void success(Response result, Response response) {

                        try {

                            // to get response in form of json
                            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            String resp;
                            resp = reader.readLine();
                            Log.d("success", "" + resp);

                            JSONObject jObj = new JSONObject(resp);
                            int success = jObj.getInt("success");


                            if(success == 1){
                                // if data stored successfully
                                //Toast.makeText(getApplicationContext(), "Added successfully", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getActivity().getApplicationContext(), FindServiceFragment.class);
                                startActivity(intent);

                            } else{
                                // if failure happened during insertion of data
                                Toast.makeText(getActivity().getApplicationContext(), "Unable to perform buy service operation", Toast.LENGTH_SHORT).show();
                            }


                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

    }
}
