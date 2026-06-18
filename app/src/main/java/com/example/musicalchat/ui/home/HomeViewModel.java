package com.example.musicalchat.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicalchat.data.model.Track;
import com.example.musicalchat.data.repository.TrackRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private final TrackRepository trackRepository;

    public HomeViewModel() {
        this.trackRepository = new TrackRepository();
    }

    public LiveData<List<Track>> searchTracks(String query) {
        return trackRepository.searchTracks(query);
    }
}
