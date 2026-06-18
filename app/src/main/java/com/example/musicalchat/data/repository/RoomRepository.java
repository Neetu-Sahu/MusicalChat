package com.example.musicalchat.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicalchat.data.model.Message;
import com.example.musicalchat.data.model.PlaybackState;
import com.example.musicalchat.data.model.Room;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.List;

public class RoomRepository {
    private final FirebaseFirestore firestore;

    public RoomRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public LiveData<List<Room>> getPublicRooms() {
        MutableLiveData<List<Room>> data = new MutableLiveData<>();
        firestore.collection("rooms")
                .whereEqualTo("public", true)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        data.setValue(value.toObjects(Room.class));
                    }
                });
        return data;
    }

    public void createRoom(Room room) {
        firestore.collection("rooms").document(room.getRoomId()).set(room);
    }

    public LiveData<List<Message>> getMessages(String roomId) {
        MutableLiveData<List<Message>> data = new MutableLiveData<>();
        firestore.collection("rooms").document(roomId).collection("messages")
                .orderBy("sentAt", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        data.setValue(value.toObjects(Message.class));
                    }
                });
        return data;
    }

    public void sendMessage(String roomId, Message message) {
        firestore.collection("rooms").document(roomId).collection("messages").add(message);
    }

    public LiveData<PlaybackState> getPlaybackState(String roomId) {
        MutableLiveData<PlaybackState> data = new MutableLiveData<>();
        firestore.collection("rooms").document(roomId).collection("playback")
                .document("current")
                .addSnapshotListener((value, error) -> {
                    if (value != null && value.exists()) {
                        data.setValue(value.toObject(PlaybackState.class));
                    }
                });
        return data;
    }

    public void updatePlaybackState(String roomId, PlaybackState state) {
        firestore.collection("rooms").document(roomId).collection("playback")
                .document("current")
                .set(state, SetOptions.merge());
    }
}
