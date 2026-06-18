package com.example.musicalchat.data.remote;

import com.example.musicalchat.data.model.Track;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JamendoResponse {
    @SerializedName("results")
    private List<Track> tracks;

    public List<Track> getTracks() {
        return tracks;
    }
}
