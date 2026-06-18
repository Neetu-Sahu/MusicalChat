package com.example.musicalchat.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicalchat.data.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;

    public AuthViewModel() {
        this.authRepository = new AuthRepository();
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return authRepository.getUserLiveData();
    }

    public void saveUserToFirestore(FirebaseUser user) {
        authRepository.saveUserToFirestore(user);
    }

    public void updateCurrentUser() {
        authRepository.updateCurrentUser();
    }
}
