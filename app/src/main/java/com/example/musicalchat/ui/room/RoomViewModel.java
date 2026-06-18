package com.example.musicalchat.ui.room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicalchat.data.model.Message;
import com.example.musicalchat.data.model.PlaybackState;
import com.example.musicalchat.data.model.Room;
import com.example.musicalchat.data.repository.RoomRepository;

import java.util.List;

public class RoomViewModel extends ViewModel {
    private final RoomRepository roomRepository;

    public RoomViewModel() {
        this.roomRepository = new RoomRepository();
    }

    public LiveData<List<Room>> getPublicRooms() {
        return roomRepository.getPublicRooms();
    }

    public void createRoom(Room room) {
        roomRepository.createRoom(room);
    }

    public LiveData<List<Message>> getMessages(String roomId) {
        return roomRepository.getMessages(roomId);
    }

    public void sendMessage(String roomId, Message message) {
        roomRepository.sendMessage(roomId, message);
    }

    public LiveData<PlaybackState> getPlaybackState(String roomId) {
        return roomRepository.getPlaybackState(roomId);
    }

    public void updatePlaybackState(String roomId, PlaybackState state) {
        roomRepository.updatePlaybackState(roomId, state);
    }
}
