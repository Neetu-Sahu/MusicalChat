package com.example.musicalchat.data.api;

import com.example.musicalchat.data.model.JamendoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JamendoService {
    @GET("tracks/")
    default Call<JamendoResponse> getTracks(
            @Query("client_id") String clientId,
            @Query("format") String format,
            @Query("limit") int limit,
            @Query("search") String search,
            @Query("audioformat") String audioFormat,
            @Query("include") String include,
            @Query("imagesize") int imageSize
    ) {
        return null;
    }
}
