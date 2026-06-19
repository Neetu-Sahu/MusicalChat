package com.example.musicalchat.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PlaybackState {
    private String trackId;
    private boolean playing;
    private long positionMs;
    private Timestamp lastUpdated;

    // Fields for Realtime Database compatibility
    public String current_track_url;
    public String current_track_title;
    public boolean is_playing;
    public long current_position_ms;
    public long last_updated_system_time;

    public PlaybackState() {
        // Default constructor required for calls to DataSnapshot.getValue(PlaybackState.class)
    }

    public PlaybackState(String trackId, boolean playing, long positionMs, Timestamp lastUpdated) {
        this.trackId = trackId;
        this.playing = playing;
        this.positionMs = positionMs;
        this.lastUpdated = lastUpdated;
        
        // Sync older fields
        this.current_track_url = trackId;
        this.is_playing = playing;
        this.current_position_ms = positionMs;
        if (lastUpdated != null) {
            this.last_updated_system_time = lastUpdated.toDate().getTime();
        }
    }

    public PlaybackState(String current_track_url, String current_track_title, boolean is_playing, long current_position_ms, long last_updated_system_time) {
        this.current_track_url = current_track_url;
        this.current_track_title = current_track_title;
        this.is_playing = is_playing;
        this.current_position_ms = current_position_ms;
        this.last_updated_system_time = last_updated_system_time;
        
        // Sync newer fields
        this.trackId = current_track_url;
        this.playing = is_playing;
        this.positionMs = current_position_ms;
        this.lastUpdated = new Timestamp(new java.util.Date(last_updated_system_time));
    }

    // Getters and Setters for SyncEngine and Firestore
    public String getTrackId() {
        return trackId != null ? trackId : current_track_url;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
        this.current_track_url = trackId;
    }

    public boolean isPlaying() {
        return playing || is_playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
        this.is_playing = playing;
    }

    public long getPositionMs() {
        return positionMs != 0 ? positionMs : current_position_ms;
    }

    public void setPositionMs(long positionMs) {
        this.positionMs = positionMs;
        this.current_position_ms = positionMs;
    }

    public Timestamp getLastUpdated() {
        if (lastUpdated != null) return lastUpdated;
        return new Timestamp(new java.util.Date(last_updated_system_time));
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
        if (lastUpdated != null) {
            this.last_updated_system_time = lastUpdated.toDate().getTime();
        }
    }
}
