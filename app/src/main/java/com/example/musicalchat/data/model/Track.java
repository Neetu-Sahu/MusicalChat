package com.example.musicalchat.data.model;

import com.google.gson.annotations.SerializedName;

public class Track {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String title;
    
    @SerializedName("artist_name")
    private String artist;
    
    @SerializedName("duration")
    private int durationSeconds;
    
    @SerializedName("audio")
    private String audioUrl;
    
    @SerializedName("image")
    private String coverArtUrl;

    public Track() {
    }

    public Track(String id, String title, String artist, String audioUrl) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.audioUrl = audioUrl;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getDurationSeconds() { return durationSeconds; }
    public String getAudioUrl() { return audioUrl; }
    public String getCoverArtUrl() { return coverArtUrl; }
    
    public long getDurationMs() {
        return durationSeconds * 1000L;
    }
}
