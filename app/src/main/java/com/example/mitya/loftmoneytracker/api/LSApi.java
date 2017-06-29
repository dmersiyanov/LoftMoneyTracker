package com.example.mitya.loftmoneytracker.api;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Mitya on 29.06.2017.
 */

public interface LSApi {
    @POST("items/add")
    Call<AddResult> add(@Query("name") String name,
                        @Query("price") int price, @Query("type") String type);

    @POST("items/remove")
    Call<Result> remove(@Query("id") int id);

}
