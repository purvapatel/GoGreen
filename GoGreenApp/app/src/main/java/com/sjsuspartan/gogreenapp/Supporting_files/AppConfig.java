package com.sjsuspartan.gogreenapp.Supporting_files;

/**
 * Created by purvapatel on 12/2/17.
 */

import java.util.HashMap;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public class AppConfig {

    // check username and password during login
    public interface signin {
        @GET("/userlist/{email}/{password}")
        void get_userlist(
                @Path("email") String email,
                @Path("password") String password,
                Callback<Response> callback);
    }

    // add user registration info into database
    public interface adduserprofile {
        @POST("/userlist")
        void add_user_profile(
                @Body HashMap<String, Object> body,
                Callback<Response> callback);
    }

    // get name and _id form userlist
    public interface CustomerList {
        @GET("/userlist")
        void get_cust_name(
                Callback<Response> callback);

    }

    // check username and password during login
    public interface UserDetails {
        @GET("/userlist/{name}")
        void get_userdetail(
                @Path("name") String name,
                Callback<Response> callback);

    }

    // delete user by _id
    public interface DeleteUser{
        @DELETE("/userlist/{id}")

        void delete_user(
                @Path("id") String id,
                Callback<Response> callback);
    }

    // add service details with supplier _id and name
    public interface AddService {
        @POST("/servicelist")
        void add_service(
                @Body HashMap<String, Object> body,
                Callback<Response> callback);
    }

    // get service list by supplier name
    public interface ServiceDetails {
        @GET("/servicelist/{supplier_name}")
        void get_servicedetail(
                @Path("supplier_name") String supplier_name,
                Callback<Response> callback);

    }


}
