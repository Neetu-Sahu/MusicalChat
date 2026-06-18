package com.example.musicalchat.ui.room;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicalchat.data.model.Room;
import com.example.musicalchat.databinding.ActivityRoomsBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.UUID;

public class RoomsActivity extends AppCompatActivity {
    private ActivityRoomsBinding binding;
    private RoomViewModel roomViewModel;
    private RoomAdapter roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoomsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        setupRecyclerView();

        roomViewModel.getPublicRooms().observe(this, rooms -> {
            if (rooms != null) {
                roomAdapter.setRooms(rooms);
            }
        });

        binding.btnCreateRoom.setOnClickListener(v -> showCreateRoomDialog());
        binding.btnJoinById.setOnClickListener(v -> showJoinRoomDialog());
    }

    private void showJoinRoomDialog() {
        EditText etId = new EditText(this);
        etId.setHint("Room ID");

        new AlertDialog.Builder(this)
                .setTitle("Join Room")
                .setView(etId)
                .setPositiveButton("Join", (dialog, which) -> {
                    String id = etId.getText().toString();
                    if (!id.isEmpty()) {
                        // In a real app, we'd verify the room exists and get its metadata
                        Intent intent = new Intent(this, RoomActivity.class);
                        intent.putExtra("ROOM_ID", id);
                        intent.putExtra("ROOM_NAME", "Private Room");
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupRecyclerView() {
        roomAdapter = new RoomAdapter(room -> {
            Intent intent = new Intent(this, RoomActivity.class);
            intent.putExtra("ROOM_ID", room.getRoomId());
            intent.putExtra("ROOM_NAME", room.getName());
            intent.putExtra("HOST_UID", room.getHostUid());
            startActivity(intent);
        });
        binding.rvRooms.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRooms.setAdapter(roomAdapter);
    }

    private void showCreateRoomDialog() {
        EditText etName = new EditText(this);
        etName.setHint("Room Name");

        new AlertDialog.Builder(this)
                .setTitle("Create Room")
                .setView(etName)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = etName.getText().toString();
                    if (!name.isEmpty()) {
                        String roomId = UUID.randomUUID().toString();
                        String hostUid = FirebaseAuth.getInstance().getUid();
                        Room room = new Room(roomId, name, hostUid, true);
                        roomViewModel.createRoom(room);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
