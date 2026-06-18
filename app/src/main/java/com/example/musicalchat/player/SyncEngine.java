package com.example.musicalchat.player;

import android.util.Log;

import com.example.musicalchat.data.model.PlaybackState;
import com.google.firebase.Timestamp;

public class SyncEngine {
    private static final String TAG = "SyncEngine";
    private static final long DRIFT_THRESHOLD_MS = 500;

    public interface SyncListener {
        void onSyncRequest(String trackId, long positionMs, boolean isPlaying);
    }

    private final PlayerManager playerManager;
    private final SyncListener syncListener;

    public SyncEngine(PlayerManager playerManager, SyncListener syncListener) {
        this.playerManager = playerManager;
        this.syncListener = syncListener;
    }

    public void reconcile(PlaybackState remoteState) {
        if (remoteState == null || remoteState.getLastUpdated() == null) return;

        long remotePosition = remoteState.getPositionMs();
        boolean remoteIsPlaying = remoteState.isPlaying();
        Timestamp lastUpdated = remoteState.getLastUpdated();

        long now = System.currentTimeMillis();
        long serverTime = lastUpdated.toDate().getTime();
        long timeSinceUpdate = now - serverTime;

        long expectedPosition = remoteIsPlaying ? remotePosition + timeSinceUpdate : remotePosition;
        long localPosition = playerManager.getCurrentPosition();

        long drift = Math.abs(expectedPosition - localPosition);

        Log.d(TAG, "Reconciling: Expected=" + expectedPosition + ", Local=" + localPosition + ", Drift=" + drift);

        // If track changed or drift is too high, or play/pause state mismatched
        boolean trackChanged = remoteState.getTrackId() != null && 
                (playerManager.getCurrentTrack() == null || !remoteState.getTrackId().equals(playerManager.getCurrentTrack().getId()));
        
        boolean playStateMismatched = playerManager.isPlaying() != remoteIsPlaying;

        if (trackChanged || drift > DRIFT_THRESHOLD_MS || playStateMismatched) {
            syncListener.onSyncRequest(remoteState.getTrackId(), expectedPosition, remoteIsPlaying);
        }
    }
}
