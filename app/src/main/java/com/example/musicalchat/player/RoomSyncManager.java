package com.example.musicalchat.player;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.musicalchat.data.model.PlaybackState;
import com.example.musicalchat.data.model.RoomMeta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RoomSyncManager {
    private static final String TAG = "RoomSyncManager";
    private final String roomId;
    private final String userId;
    private final ExoPlayer player;
    private final DatabaseReference roomRef;
    private final DatabaseReference playbackRef;
    private final DatabaseReference metaRef;
    private final DatabaseReference presenceRef;

    private boolean isHost = false;
    private final Handler hostWriteHandler = new Handler(Looper.getMainLooper());
    private ValueEventListener playbackListener;
    private ValueEventListener metaListener;

    public RoomSyncManager(String roomId, String userId, ExoPlayer player) {
        this.roomId = roomId;
        this.userId = userId;
        this.player = player;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        this.roomRef = db.getReference("rooms").child(roomId);
        this.playbackRef = roomRef.child("playback_state");
        this.metaRef = roomRef.child("room_meta");
        this.presenceRef = roomRef.child("presence").child(userId);
    }

    public void joinRoom() {
        // 3. Firebase Presence
        presenceRef.setValue(true);
        presenceRef.onDisconnect().removeValue();

        // Observe meta to know if we are host or need to fallback
        metaListener = metaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RoomMeta meta = snapshot.getValue(RoomMeta.class);
                if (meta != null) {
                    boolean wasHost = isHost;
                    isHost = userId.equals(meta.host_id);
                    
                    if (isHost && !wasHost) {
                        startHostLoop();
                    } else if (!isHost && wasHost) {
                        stopHostLoop();
                    }

                    if (!isHost && meta.needs_new_host) {
                        handleHostFallback();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // 2. Guest Synchronization Listener
        playbackListener = playbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isHost) return; // Host doesn't sync from DB

                PlaybackState state = snapshot.getValue(PlaybackState.class);
                if (state != null) {
                    long now = System.currentTimeMillis();
                    long latency = now - state.last_updated_system_time;
                    long targetPos = state.current_position_ms + latency;
                    long currentPos = player.getCurrentPosition();
                    long drift = Math.abs(currentPos - targetPos);

                    if (drift > 1200) {
                        player.seekTo(targetPos);
                    }

                    if (state.is_playing != player.isPlaying()) {
                        if (state.is_playing) player.play();
                        else player.pause();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Add player listener for immediate host updates
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isHost) updatePlaybackStateImmediate();
            }

            @Override
            public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition, @NonNull Player.PositionInfo newPosition, int reason) {
                if (isHost) updatePlaybackStateImmediate();
            }

            @Override
            public void onMediaItemTransition(@Nullable androidx.media3.common.MediaItem mediaItem, int reason) {
                if (isHost) updatePlaybackStateImmediate();
            }
        });
    }

    // 1. Host Playback Writer
    private void startHostLoop() {
        hostWriteHandler.post(hostWriteRunnable);
        
        // Configure host fallback on disconnect
        metaRef.child("needs_new_host").onDisconnect().setValue(true);
        metaRef.child("host_disconnected_at").onDisconnect().setValue(ServerValue.TIMESTAMP);
    }

    private void stopHostLoop() {
        hostWriteHandler.removeCallbacks(hostWriteRunnable);
        metaRef.child("needs_new_host").onDisconnect().cancel();
        metaRef.child("host_disconnected_at").onDisconnect().cancel();
    }

    private final Runnable hostWriteRunnable = new Runnable() {
        @Override
        public void run() {
            updatePlaybackStateImmediate();
            hostWriteHandler.postDelayed(this, 5000);
        }
    };

    private void updatePlaybackStateImmediate() {
        if (!isHost) return;
        
        String url = "";
        String title = "";
        if (player.getCurrentMediaItem() != null && player.getCurrentMediaItem().localConfiguration != null) {
            url = player.getCurrentMediaItem().localConfiguration.uri.toString();
            if (player.getCurrentMediaItem().mediaMetadata.title != null) {
                title = player.getCurrentMediaItem().mediaMetadata.title.toString();
            }
        }
        
        PlaybackState state = new PlaybackState(
            url,
            title,
            player.isPlaying(),
            player.getCurrentPosition(),
            System.currentTimeMillis()
        );
        playbackRef.setValue(state);
    }

    // 4. Guest Fallback Multi-Client Listener
    private void handleHostFallback() {
        long delay = (long) (Math.random() * 3000);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            metaRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    RoomMeta meta = currentData.getValue(RoomMeta.class);
                    if (meta != null && meta.needs_new_host) {
                        meta.host_id = userId;
                        meta.needs_new_host = false;
                        currentData.setValue(meta);
                        return Transaction.success(currentData);
                    }
                    return Transaction.abort();
                }

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                    if (committed) {
                        Log.d(TAG, "Successfully took over as host");
                    }
                }
            });
        }, delay);
    }

    public void leaveRoom() {
        stopHostLoop();
        if (playbackListener != null) playbackRef.removeEventListener(playbackListener);
        if (metaListener != null) metaRef.removeEventListener(metaListener);
        presenceRef.removeValue();
    }
}
