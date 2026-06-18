package com.example.musicalchat.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicalchat.data.model.Track;
import com.example.musicalchat.data.remote.JamendoResponse;
import com.example.musicalchat.data.remote.JamendoService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrackRepository {
    private static final String BASE_URL = "https://api.jamendo.com/";
    private static final String CLIENT_ID = "651c2593"; // Updated with user's Jamendo Client ID
    
    private final JamendoService jamendoService;

    public TrackRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jamendoService = retrofit.create(JamendoService.class);
    }

    public LiveData<List<Track>> searchTracks(String query) {
        MutableLiveData<List<Track>> data = new MutableLiveData<>();
        jamendoService.getTracks(CLIENT_ID, "json", 20, query).enqueue(new Callback<JamendoResponse>() {
            @Override
            public void onResponse(Call<JamendoResponse> call, Response<JamendoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body().getTracks());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<JamendoResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
