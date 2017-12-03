package com.sjsuspartan.gogreenapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class ViewServiceActivity extends AppCompatActivity {

    public String supp_name  = "";
    public String supp_id = "";

    ListView list;

    String[] serviceList = {
    };

    String[] serviceIdList = {
    };

    final Context c = this;

    // url for api call
    String BASE_URL = "https://gogreen-spartan-app.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_service);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            supp_name = extras.getString("name", "");
            supp_id = extras.getString("id", "");
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // set icon on action bar
        getSupportActionBar().setIcon(R.drawable.sjsu);
        // set text on action bar
        getSupportActionBar().setTitle("    Go Green App");


        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .setClient(new OkClient(new OkHttpClient()));

        RestAdapter adapter = builder.build();

        AppConfig.ServiceDetails api = adapter.create(AppConfig.ServiceDetails.class);

        api.get_servicedetail(
                supp_name,
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
                                    Toast.makeText(getApplicationContext(), "Customer data not available", Toast.LENGTH_SHORT).show();
                                    Log.d("JsonException", e.toString());
                                }
                            }

                            serviceList = arr_name.toArray(new String[arr_name.size()]);
                            serviceIdList = arr_name.toArray(new String[arr_id.size()]);


                            // List adapter for list view
                            CustomListAdapter listadapter = new CustomListAdapter(ViewServiceActivity.this, serviceList);
                            //get the reference of the list view from activity_main
                            list = (ListView) findViewById(R.id.list);
                            //set adapterview with listview
                            list.setAdapter(listadapter);


                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {

                                    Log.d("id =========",""+serviceIdList[position]);

                                    RestAdapter.Builder builder = new RestAdapter.Builder()
                                            .setEndpoint(BASE_URL) //Setting the Root URL
                                            .setClient(new OkClient(new OkHttpClient()));

                                    RestAdapter adapter = builder.build();

                                    AppConfig.UserDetails api = adapter.create(AppConfig.UserDetails.class);

                                    /*api.get_userdetail(
                                            customerList[position],
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

                                                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);

                                                        //provide custom dialog view.
                                                        View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                                                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);

                                                        // set view to the current context.
                                                        alertDialogBuilderUserInput.setView(mView);

                                                        final EditText name = (EditText) mView.findViewById(R.id.comment_name);
                                                        final EditText email = (EditText) mView.findViewById(R.id.comment_email);
                                                        final EditText mobile = (EditText) mView.findViewById(R.id.comment_mobile);

                                                        name.setText(obj.getString("name"));
                                                        email.setText(obj.getString("email"));
                                                        mobile.setText(obj.getString("mobile"));

                                                        final String user_id = obj.getString("_id");

                                                        // store feedback information into database
                                                        alertDialogBuilderUserInput
                                                                .setCancelable(false)
                                                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialogBox, int id) {
                                                                        // ToDo get user input here

                                                                        RestAdapter.Builder builder = new RestAdapter.Builder()
                                                                                .setEndpoint(BASE_URL) //Setting the Root URL
                                                                                .setClient(new OkClient(new OkHttpClient()));

                                                                        RestAdapter adapter = builder.build();

                                                                        //get reference of the interface
                                                                        AppConfig.DeleteUser api = adapter.create(AppConfig.DeleteUser.class);


                                                                        // call method to store data
                                                                        // pass map and Callback as a parameter
                                                                        api.delete_user(
                                                                                user_id,
                                                                                new Callback<Response>() {
                                                                                    @Override
                                                                                    public void success(Response result, Response response) {

                                                                                        try {

                                                                                            //retrive json response
                                                                                            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                                                                                            String resp;
                                                                                            resp = reader.readLine();

                                                                                            // get the value of success fron json response
                                                                                            //Log.d("success", "" + resp);
                                                                                            finish();
                                                                                            Intent intent = new Intent(getApplicationContext(), SupplierActivity.class);
                                                                                            startActivity(intent);


                                                                                        } catch (IOException e) {
                                                                                            Log.d("Exception", e.toString());
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void failure(RetrofitError error) {
                                                                                        Toast.makeText(SupplierActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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




                                                    } catch (IOException e) {
                                                        Log.d("Exception", e.toString());
                                                    }catch (JSONException e) {
                                                        Log.d("JsonException", e.toString());
                                                    }
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    Toast.makeText(SupplierActivity.this, "Data not found", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                    );*/



                                }

                            });


                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Customer data not available", Toast.LENGTH_SHORT).show();
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(ViewServiceActivity.this, "Data not found", Toast.LENGTH_LONG).show();
                    }
                }
        );

    }
}
