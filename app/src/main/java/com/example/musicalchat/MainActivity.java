package com.example.musicalchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicalchat.databinding.ActivityMainBinding;
import com.example.musicalchat.player.PlayerManager;
import com.example.musicalchat.ui.auth.LoginActivity;
import com.example.musicalchat.ui.home.HomeViewModel;
import com.example.musicalchat.ui.home.TrackAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private HomeViewModel homeViewModel;
    private PlayerManager playerManager;
    private TrackAdapter trackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        playerManager = PlayerManager.getInstance(this);

        setupRecyclerView();

        binding.btnSearch.setOnClickListener(v -> {
            String query = binding.etSearch.getText().toString();
            if (!query.isEmpty()) {
                homeViewModel.searchTracks(query).observe(this, tracks -> {
                    if (tracks != null) {
                        trackAdapter.setTracks(tracks);
                    } else {
                        Toast.makeText(this, "Search failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.btnRooms.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.musicalchat.ui.room.RoomsActivity.class));
        });

        binding.btnPlayPause.setOnClickListener(v -> {
            if (playerManager.isPlaying()) {
                playerManager.pause();
                binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            } else {
                playerManager.resume();
                binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupRecyclerView() {
        trackAdapter = new TrackAdapter(track -> {
            playerManager.playTrack(track);
            binding.tvCurrentTrack.setText(track.getTitle() + " - " + track.getArtist());
            binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        });
        binding.rvTracks.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTracks.setAdapter(trackAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            playerManager.release();
        }
    }
}
