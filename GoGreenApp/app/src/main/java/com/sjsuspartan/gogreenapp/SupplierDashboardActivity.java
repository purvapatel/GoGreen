package com.sjsuspartan.gogreenapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sjsuspartan.gogreenapp.Supporting_files.AppConfig;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class SupplierDashboardActivity extends AppCompatActivity {

    public String supp_name;
    public String supp_id;

    String BASE_URL = "https://gogreen-spartan-app.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_dashboard);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            supp_name = extras.getString("name","NA");
            supp_id = extras.getString("id","NA");
        }
    }

    public void customer(View view){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), SupplierActivity.class);
        startActivity(intent);
    }

    public void view_service(View view){

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), ViewServiceActivity.class);
        intent.putExtra("name",supp_name);
        intent.putExtra("id",supp_id);
        startActivity(intent);
    }

    public void add_service(View view){

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(SupplierDashboardActivity.this);

        //provide custom dialog view.
        View mView = layoutInflaterAndroid.inflate(R.layout.add_service_dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(SupplierDashboardActivity.this);

        // set view to the current context.
        alertDialogBuilderUserInput.setView(mView);

        final EditText name = (EditText) mView.findViewById(R.id.service_name);
        final EditText location = (EditText) mView.findViewById(R.id.service_location);
        final EditText rate = (EditText) mView.findViewById(R.id.service_rate);

        // store service data information into database
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here

                        RestAdapter.Builder builder = new RestAdapter.Builder()
                                .setEndpoint(BASE_URL) //Setting the Root URL
                                .setClient(new OkClient(new OkHttpClient()));

                        RestAdapter adapter = builder.build();

                        //get reference of the interface
                        AppConfig.AddService api = adapter.create(AppConfig.AddService.class);

                        // to store into database
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("supplier_id", supp_id);
                        map.put("supplier_name", supp_name);
                        map.put("name", name.getText().toString());
                        map.put("location", location.getText().toString());
                        map.put("rate", rate.getText().toString());

                        // call method to store data
                        // pass map and Callback as a parameter
                        api.add_service(
                                map,
                                new Callback<Response>() {
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

                                                Intent intent = new Intent(getApplicationContext(), SupplierDashboardActivity.class);
                                                startActivity(intent);

                                            } else{
                                                // if failure happened during insertion of data
                                                Toast.makeText(getApplicationContext(), "Unable to add Service Data", Toast.LENGTH_SHORT).show();
                                            }


                                        } catch (IOException e) {
                                            Log.d("Exception", e.toString());
                                        }catch (JSONException e) {
                                            Log.d("JsonException", e.toString());
                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Toast.makeText(SupplierDashboardActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                        );

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



    }
}
