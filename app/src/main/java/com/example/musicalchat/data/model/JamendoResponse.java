package com.example.musicalchat.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JamendoResponse {
    @SerializedName("results")
    private List<Track> tracks;

    public List<Track> getTracks() {
        return tracks;
    }

    public static class Track {
        private String id;
        private String name;
        @SerializedName("artist_name")
        private String artistName;
        private String audio;
        private String image;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getArtistName() { return artistName; }
        public String getAudio() { return audio; }
        public String getImage() { return image; }
    }
}
