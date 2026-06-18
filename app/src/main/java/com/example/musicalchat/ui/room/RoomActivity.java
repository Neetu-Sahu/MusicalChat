package com.example.musicalchat.ui.room;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicalchat.data.model.Message;
import com.example.musicalchat.data.model.PlaybackState;
import com.example.musicalchat.databinding.ActivityRoomBinding;
import com.example.musicalchat.player.PlayerManager;
import com.example.musicalchat.player.SyncEngine;
import com.google.firebase.auth.FirebaseAuth;

public class RoomActivity extends AppCompatActivity {
    private ActivityRoomBinding binding;
    private RoomViewModel roomViewModel;
    private ChatAdapter chatAdapter;
    private String roomId;
    private String hostUid;
    private PlayerManager playerManager;
    private SyncEngine syncEngine;
    private boolean isHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        roomId = getIntent().getStringExtra("ROOM_ID");
        String roomName = getIntent().getStringExtra("ROOM_NAME");
        hostUid = getIntent().getStringExtra("HOST_UID");
        binding.tvRoomName.setText(roomName);

        String currentUid = FirebaseAuth.getInstance().getUid();
        isHost = currentUid != null && currentUid.equals(hostUid);

        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        playerManager = PlayerManager.getInstance(this);
        
        if (isHost) {
            playerManager.setListener(new PlayerManager.PlayerStateListener() {
                @Override
                public void onPlaybackStateChanged(boolean isPlaying) {
                    updateRemotePlaybackState();
                }

                @Override
                public void onPositionDiscontinuity() {
                    updateRemotePlaybackState();
                }
            });
        }
        
        syncEngine = new SyncEngine(playerManager, (trackId, positionMs, isPlaying) -> {
            if (!isHost) {
                // For now, we don't have getTrackById, so we rely on audioUrl being in PlaybackState
                // This logic will be handled when we receive the state update
            }
        });

        setupRecyclerView();

        roomViewModel.getMessages(roomId).observe(this, messages -> {
            if (messages != null) {
                chatAdapter.setMessages(messages);
                binding.rvMessages.scrollToPosition(messages.size() - 1);
            }
        });

        roomViewModel.getPlaybackState(roomId).observe(this, state -> {
            if (state != null && !isHost) {
                syncWithRemote(state);
            }
        });

        binding.btnSend.setOnClickListener(v -> {
            String text = binding.etMessage.getText().toString();
            if (!text.isEmpty()) {
                String uid = FirebaseAuth.getInstance().getUid();
                Message message = new Message(null, uid, text, "text");
                roomViewModel.sendMessage(roomId, message);
                binding.etMessage.setText("");
            }
        });

        binding.btnPlayPause.setOnClickListener(v -> {
            if (isHost) {
                if (playerManager.isPlaying()) {
                    playerManager.pause();
                } else {
                    playerManager.resume();
                }
                updateRemotePlaybackState();
            }
        });

        updatePlayerUI();
    }

    private void syncWithRemote(PlaybackState state) {
        if (state.getTrackId() != null && (playerManager.getCurrentTrack() == null || 
                !state.getTrackId().equals(playerManager.getCurrentTrack().getId()))) {
            // Track changed, play it
            com.example.musicalchat.data.model.Track track = new com.example.musicalchat.data.model.Track(
                    state.getTrackId(),
                    state.getTrackTitle(),
                    "Remote Artist", // Placeholder
                    state.getAudioUrl()
            );
            playerManager.playTrack(track);
        }
        
        if (state.isPlaying() && !playerManager.isPlaying()) {
            playerManager.resume();
        } else if (!state.isPlaying() && playerManager.isPlaying()) {
            playerManager.pause();
        }
        
        // Drift correction
        long drift = Math.abs(state.getPositionMs() - playerManager.getCurrentPosition());
        if (drift > 1000) {
            playerManager.seekTo(state.getPositionMs());
        }
        updatePlayerUI();
    }

    private void updateRemotePlaybackState() {
        if (!isHost || playerManager.getCurrentTrack() == null) return;

        PlaybackState state = new PlaybackState(
                playerManager.getCurrentTrack().getId(),
                playerManager.getCurrentTrack().getAudioUrl(),
                playerManager.getCurrentTrack().getTitle(),
                playerManager.isPlaying(),
                playerManager.getCurrentPosition(),
                hostUid
        );
        roomViewModel.updatePlaybackState(roomId, state);
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter();
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMessages.setAdapter(chatAdapter);
    }

    private void updatePlayerUI() {
        if (playerManager.getCurrentTrack() != null) {
            binding.tvCurrentTrack.setText(playerManager.getCurrentTrack().getTitle());
            binding.btnPlayPause.setImageResource(playerManager.isPlaying() ? 
                    android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
        }
    }
}
