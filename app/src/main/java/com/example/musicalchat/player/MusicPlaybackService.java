package com.example.musicalchat.player;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

public class MusicPlaybackService extends MediaSessionService {

    private ExoPlayer player;
    private MediaSession mediaSession;
    private RoomSyncManager syncManager;

    @Override
    public void onCreate() {
        super.onCreate();
        
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build();

        player = new ExoPlayer.Builder(this)
                .setAudioAttributes(audioAttributes, true)
                .build();

        mediaSession = new MediaSession.Builder(this, player).build();
    }

    @Override
    public void onDestroy() {
        if (syncManager != null) {
            syncManager.leaveRoom();
        }
        mediaSession.release();
        player.release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if ("JOIN_ROOM".equals(action)) {
                String roomId = intent.getStringExtra("ROOM_ID");
                SharedPreferences prefs = getSharedPreferences("MusicalChatPrefs", MODE_PRIVATE);
                String userId = prefs.getString("user_uid", null);
                
                if (roomId != null && userId != null) {
                    if (syncManager != null) syncManager.leaveRoom();
                    syncManager = new RoomSyncManager(roomId, userId, player);
                    syncManager.joinRoom();
                }
            } else if ("LEAVE_ROOM".equals(action)) {
                if (syncManager != null) {
                    syncManager.leaveRoom();
                    syncManager = null;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
