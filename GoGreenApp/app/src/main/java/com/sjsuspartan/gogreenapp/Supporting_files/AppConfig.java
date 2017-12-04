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
import retrofit.http.PUT;
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

    // get user info by user name
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

    // check username and password during login
    public interface ServiceDetail {
        @GET("/servicedetails/{id}")
        void get_servicedetail_byid(
                @Path("id") String id,
                Callback<Response> callback);

    }

    // delete service by _id
    public interface DeleteService{
        @DELETE("/servicelist/{id}")

        void delete_service(
                @Path("id") String id,
                Callback<Response> callback);
    }

    // update service by ID
    public interface UpdateService {
        @PUT("/servicelist/{id}")
        void update_service(@Path("id") String id,
                            @Body HashMap<String, Object> body,
                            Callback<Response> callback);
    }

    //get supplier name for customers
    public interface SupplierList {
        @GET("/supplierlist")
        void get_supp_name(
                Callback<Response> callback);

    }

    //get services for customers
    public interface ServiceList {
        @GET("/servicelist")
        void get_all_services(
                Callback<Response> callback);

    }

    // add user_service details when user wants to buy any service
    public interface BuyService {
        @POST("/addUserServiceList")
        void add_user_service(
                @Body HashMap<String, Object> body,
                Callback<Response> callback);
    }

    public interface MyServices {
        @GET("/findServiceByUserName/{user_name}")
        void get_my_services(
                @Path("user_name") String user_name,
                Callback<Response> callback);

    }
}
