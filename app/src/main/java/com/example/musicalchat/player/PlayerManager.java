package com.example.musicalchat.player;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.musicalchat.data.model.Track;

public class PlayerManager {
    private static PlayerManager instance;
    private final ExoPlayer exoPlayer;
    private Track currentTrack;

    public interface PlayerStateListener {
        void onPlaybackStateChanged(boolean isPlaying);
        void onPositionDiscontinuity();
    }

    private PlayerStateListener listener;

    private PlayerManager(Context context) {
        exoPlayer = new ExoPlayer.Builder(context).build();
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (listener != null) listener.onPlaybackStateChanged(isPlaying);
            }

            @Override
            public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition, @NonNull Player.PositionInfo newPosition, int reason) {
                if (listener != null) listener.onPositionDiscontinuity();
            }
        });
    }

    public void setListener(PlayerStateListener listener) {
        this.listener = listener;
    }

    public static synchronized PlayerManager getInstance(Context context) {
        if (instance == null) {
            instance = new PlayerManager(context.getApplicationContext());
        }
        return instance;
    }

    public void playTrack(Track track) {
        this.currentTrack = track;
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(track.getAudioUrl()));
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();
    }

    public void pause() {
        exoPlayer.pause();
    }

    public void resume() {
        exoPlayer.play();
    }

    public void seekTo(long positionMs) {
        exoPlayer.seekTo(positionMs);
    }

    public long getCurrentPosition() {
        return exoPlayer.getCurrentPosition();
    }

    public boolean isPlaying() {
        return exoPlayer.isPlaying();
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public void release() {
        exoPlayer.release();
        instance = null;
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }
}
