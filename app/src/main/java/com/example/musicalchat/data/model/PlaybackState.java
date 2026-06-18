package com.example.musicalchat.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.List;

public class PlaybackState {
    private String trackId;
    private String audioUrl;
    private String trackTitle;
    private boolean isPlaying;
    private long positionMs;
    private String hostUid;
    private List<String> queue;
    
    @ServerTimestamp
    private Timestamp lastUpdated;

    public PlaybackState() {
        this.queue = new ArrayList<>();
    }

    public PlaybackState(String trackId, String audioUrl, String trackTitle, boolean isPlaying, long positionMs, String hostUid) {
        this.trackId = trackId;
        this.audioUrl = audioUrl;
        this.trackTitle = trackTitle;
        this.isPlaying = isPlaying;
        this.positionMs = positionMs;
        this.hostUid = hostUid;
        this.queue = new ArrayList<>();
    }

    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public String getTrackTitle() { return trackTitle; }
    public void setTrackTitle(String trackTitle) { this.trackTitle = trackTitle; }

    public boolean isPlaying() { return isPlaying; }
    public void setPlaying(boolean playing) { isPlaying = playing; }

    public long getPositionMs() { return positionMs; }
    public void setPositionMs(long positionMs) { this.positionMs = positionMs; }

    public String getHostUid() { return hostUid; }
    public void setHostUid(String hostUid) { this.hostUid = hostUid; }

    public List<String> getQueue() { return queue; }
    public void setQueue(List<String> queue) { this.queue = queue; }

    public Timestamp getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Timestamp lastUpdated) { this.lastUpdated = lastUpdated; }
}
