package com.sjsuspartan.gogreenapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.sjsuspartan.gogreenapp.Supporting_files.AppConfig;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class UserProfileFragment extends Fragment {

    EditText name;
    EditText email;
    EditText mobile;

    SharedPreferences cmpe235prefs;

    String BASE_URL = "https://gogreen-spartan-app.herokuapp.com/";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_user_profile, container , false);

        cmpe235prefs = getActivity().getSharedPreferences("cmpe235", Context.MODE_PRIVATE);
        String user_name = cmpe235prefs.getString("name","NA");

        name = (EditText) view.findViewById(R.id.name);
        email = (EditText) view.findViewById(R.id.email);
        mobile = (EditText) view.findViewById(R.id.mobile);

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .setClient(new OkClient(new OkHttpClient()));

        RestAdapter adapter = builder.build();

        AppConfig.UserDetails api = adapter.create(AppConfig.UserDetails.class);

        api.get_userdetail(
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
                            final JSONObject obj = (JSONObject) jObj.get(0);

                            name.setText(obj.getString("name"));
                            name.setEnabled(false);
                            email.setText(obj.getString("email"));
                            email.setEnabled(false);
                            mobile.setText(obj.getString("mobile"));
                            mobile.setEnabled(false);


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

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("User Profile");
    }
}