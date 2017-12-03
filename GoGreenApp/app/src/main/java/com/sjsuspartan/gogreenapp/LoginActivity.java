package com.sjsuspartan.gogreenapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 0;

    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private Button _signupLink;
    String BASE_URL = "https://gogreen-spartan-app.herokuapp.com/";

    SharedPreferences cmpe235prefs;
    SharedPreferences.Editor editor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _emailText = (EditText) findViewById(R.id.login_email);
        _passwordText = (EditText) findViewById(R.id.login_password);
        _loginButton = (Button) findViewById(R.id.btn_sign_in);
        _signupLink = (Button) findViewById(R.id.btn_new_user);

        checkUserLog();

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity

                Intent intent = new Intent(getApplicationContext(), NewUserActivity.class);
                intent.putExtra("name","");
                intent.putExtra("mobile","");
                intent.putExtra("email","");
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validate()) {
                    onLoginFailed();
                    return;
                }

                // fetch comment information from the database
                // set that info into edit text
                RestAdapter.Builder builder = new RestAdapter.Builder()
                        .setEndpoint(BASE_URL) //Setting the Root URL
                        .setClient(new OkClient(new OkHttpClient()));

                RestAdapter adapter = builder.build();

                AppConfig.signin api = adapter.create(AppConfig.signin.class);

                Log.d("email", _emailText.getText().toString());
                Log.d("password", _passwordText.getText().toString());

                api.get_userlist(
                        _emailText.getText().toString(),
                        _passwordText.getText().toString(),
                        new Callback<Response>() {
                            @Override
                            public void success(Response result, Response response) {

                                try {

                                    BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                                    String resp;
                                    resp = reader.readLine();
                                    Log.d("success", "" + resp);

                                    JSONArray jObj = new JSONArray(resp);
                                    JSONObject obj = (JSONObject) jObj.get(0);
                                    String type = obj.getString("type");

                                    Log.d("type-----", "" + type);

                                    if(jObj.length() == 1){
                                        Toast.makeText(getApplicationContext(), "Log in Successful", Toast.LENGTH_SHORT).show();


                                        Intent intent = new Intent();

                                        if(type.equals("Supplier")) {
                                            intent.setClass(LoginActivity.this, SupplierDashboardActivity.class);
                                        }else {
                                            intent.setClass(LoginActivity.this, NormalUserActivity.class);
                                        }

                                        intent.putExtra("name",obj.getString("name"));
                                        intent.putExtra("id",obj.getString("_id"));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        storeLoginData(obj.getString("name"), obj.getString("_id"), type);

                                        startActivity(intent);
                                    } else{
                                        Toast.makeText(getApplicationContext(), "Log in Fail", Toast.LENGTH_SHORT).show();
                                    }


                                } catch (IOException e) {
                                    Log.d("Exception", e.toString());
                                }catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "Invalid Usename or Password", Toast.LENGTH_SHORT).show();
                                    Log.d("JsonException", e.toString());
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(LoginActivity.this, "Invalid Usename or Password", Toast.LENGTH_LONG).show();
                            }
                        }
                );


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public void checkUserLog()
    {
        cmpe235prefs = getSharedPreferences("cmpe235", Context.MODE_PRIVATE);

        if (cmpe235prefs.getBoolean("userLogged", false))
        {
            String type = cmpe235prefs.getString("type","User");
            Intent intent;
            if(type.equals("User")) {
                intent = new Intent(this, NormalUserActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("name", cmpe235prefs.getString("name", "John"));
                intent.putExtra("id", cmpe235prefs.getString("id", ""));
            }
            else {
                intent = new Intent(this, SupplierDashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("name", cmpe235prefs.getString("name", "John"));
                intent.putExtra("id", cmpe235prefs.getString("id", ""));
            }
            startActivity(intent);
        }
    }

    private void storeLoginData(String name, String id, String type)
    {
        cmpe235prefs = getSharedPreferences("cmpe235", Context.MODE_PRIVATE);

        editor = cmpe235prefs.edit();
        editor.putBoolean("userLogged", true);
        editor.putString("name", name);
        editor.putString("id", id);
        editor.putString("type", type);
        editor.commit();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


}

