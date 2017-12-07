package com.sjsuspartan.gogreenapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sjsuspartan.gogreenapp.Supporting_files.AppConfig;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager mLocationManager;

    String[] serviceList = {
    };

    String[] serviceID = {
    };

    String[] serviceLocation =  {"San Jose", "Santa Clara", "Mountain View","San Mateo", "San Francisco"," Fremont", "San Jose", "Fremont" , "San Carlos"};

    SharedPreferences cmpe235prefs;

    String BASE_URL = "https://gogreen-spartan-app.herokuapp.com/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        cmpe235prefs = getSharedPreferences("cmpe235", Context.MODE_PRIVATE);
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

                        ArrayList<String> arr_name = new ArrayList<String>();
                        ArrayList<String> arr_id = new ArrayList<String>();
                        final ArrayList<String> arr_location = new ArrayList<String>();

                        try {

                            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            String resp;
                            resp = reader.readLine();
                            Log.d("success", "" + resp);

                            JSONArray jObj = new JSONArray(resp);

                            for (int i = 0; i < jObj.length(); i++) {
                                try {

                                    final JSONObject obj = (JSONObject) jObj.get(i);
                                    arr_name.add(obj.getString("service_name"));
                                    arr_id.add(obj.getString("service_id"));

                                    RestAdapter.Builder builder = new RestAdapter.Builder()
                                            .setEndpoint(BASE_URL) //Setting the Root URL
                                            .setClient(new OkClient(new OkHttpClient()));

                                    RestAdapter adapter = builder.build();

                                    AppConfig.ServiceDetail api = adapter.create(AppConfig.ServiceDetail.class);

                                    api.get_servicedetail_byid(
                                            obj.getString("service_id"),
                                            new Callback<Response>() {
                                                @Override
                                                public void success(Response result, Response response) {

                                                    try {

                                                        BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                                                        String resp;
                                                        resp = reader.readLine();
                                                        Log.d("success", "" + resp);

                                                        JSONArray jObj = new JSONArray(resp);
                                                        final JSONObject loc_obj = (JSONObject) jObj.get(0);

                                                        arr_location.add(loc_obj.getString("location"));

                                                    } catch (IOException e) {
                                                        Log.d("Exception", e.toString());
                                                    } catch (JSONException e) {
                                                        Toast.makeText(getApplicationContext(), "Service data not available", Toast.LENGTH_SHORT).show();
                                                        Log.d("JsonException", e.toString());
                                                    }
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                    );


                                } catch (JSONException e) {
                                    Toast.makeText(MapActivity.this, "Service data not available", Toast.LENGTH_SHORT).show();
                                    Log.d("JsonException", e.toString());
                                }
                            }

                            serviceList = arr_name.toArray(new String[arr_name.size()]);
                            serviceID = arr_id.toArray(new String[arr_id.size()]);
                            //serviceLocation = arr_location.toArray(new String[arr_location.size()]);

                            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.map);

                            mapFragment.getMapAsync(MapActivity.this);

                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Service data not available", Toast.LENGTH_SHORT).show();
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_LONG).show();
                    }
                }
        );

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


           for(int i=0; i< serviceList.length; i++)
            {
                Log.d("====>","test : "+serviceLocation[i]);
                Log.d("====>","test : "+serviceList[i]);
                //Location location = new Location(serviceLocation[i]);

                Geocoder coder = new Geocoder(getApplicationContext());
                try {
                    List<Address> address = coder.getFromLocationName(serviceLocation[i], 5);

                    Address location = address.get(0);
                    location.getLatitude();
                    location.getLongitude();

                    mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title(serviceList[i]));

                }
                catch (Exception e){}
                //if(location!= null)
                    //LatLng s_loc = new LatLng(location.getLatitude(), location.getLongitude());


            //mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here."));

            //mMap.addMarker(new MarkerOptions().position(new LatLng(37.340449, -121.895585)).title("You are here."));

            //mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        }
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }

            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
