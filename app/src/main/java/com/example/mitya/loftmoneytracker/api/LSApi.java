package com.example.mitya.loftmoneytracker.api;

import com.example.mitya.loftmoneytracker.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Mitya on 29.06.2017.
 */

public interface LSApi {

    @Headers("Content-Type: application/json")
    @GET("items")
    Call<List<Item>> items(@Query("type") String type);


    @POST("items/add")
    Call<AddResult> add(@Query("name") String name, @Query("price") int price, @Query("type") String type);


    @POST("items/remove")
    Call<Result> remove(@Query("id") int id);

    @GET("auth")
    Call<AuthResult> auth(@Query("social_user_id") String socialUserId);

}
