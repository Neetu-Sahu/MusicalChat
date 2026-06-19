package com.example.musicalchat.ui.room;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import com.example.musicalchat.databinding.ActivityRoomBinding;
import com.example.musicalchat.player.MusicPlaybackService;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RoomActivity extends AppCompatActivity {

    private ActivityRoomBinding binding;
    private MediaController mediaController;
    private ListenableFuture<MediaController> controllerFuture;
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        roomId = getIntent().getStringExtra("ROOM_ID");
        if (roomId != null) {
            Intent serviceIntent = new Intent(this, MusicPlaybackService.class);
            serviceIntent.setAction("JOIN_ROOM");
            serviceIntent.putExtra("ROOM_ID", roomId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            observeRoomMeta();
        } else {
            finish();
        }

        checkNotificationPermission();
        setupListeners();
    }

    private void observeRoomMeta() {
        FirebaseDatabase.getInstance().getReference("rooms")
                .child(roomId).child("room_meta").child("room_name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.getValue(String.class);
                        if (name != null) {
                            binding.tvRoomName.setText(name);
                            showContent();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void showContent() {
        binding.loadingSpinner.setVisibility(View.GONE);
        binding.roomContent.setVisibility(View.VISIBLE);
    }

    private void setupListeners() {
        binding.btnRoomPlayPause.setOnClickListener(v -> {
            if (mediaController != null) {
                if (mediaController.isPlaying()) {
                    mediaController.pause();
                } else {
                    mediaController.play();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeController();
    }

    private void initializeController() {
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, MusicPlaybackService.class));
        controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
            try {
                mediaController = controllerFuture.get();
                updateUiState();
                mediaController.addListener(new Player.Listener() {
                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        updateUiState();
                    }

                    @Override
                    public void onMediaMetadataChanged(@NonNull MediaMetadata mediaMetadata) {
                        updateUiState();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, MoreExecutors.directExecutor());
    }

    private void updateUiState() {
        if (mediaController == null) return;

        if (mediaController.isPlaying()) {
            binding.btnRoomPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            binding.btnRoomPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }

        MediaMetadata metadata = mediaController.getMediaMetadata();
        if (metadata.title != null) {
            binding.tvTrackStatus.setText("Playing: " + metadata.title);
        } else {
            binding.tvTrackStatus.setText("Waiting for host...");
        }
    }

    @Override
    protected void onStop() {
        if (controllerFuture != null) {
            MediaController.releaseFuture(controllerFuture);
        }
        super.onStop();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }
}
