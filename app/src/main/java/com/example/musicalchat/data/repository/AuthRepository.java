package com.example.musicalchat.data.repository;

import com.example.musicalchat.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AuthRepository {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<FirebaseUser> userLiveData;

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.userLiveData = new MutableLiveData<>(firebaseAuth.getCurrentUser());
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public void signOut() {
        firebaseAuth.signOut();
        userLiveData.postValue(null);
    }

    public void saveUserToFirestore(FirebaseUser firebaseUser) {
        if (firebaseUser == null) return;

        User user = new User(
                firebaseUser.getUid(),
                firebaseUser.getDisplayName(),
                firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null,
                firebaseUser.getEmail()
        );

        firestore.collection("users").document(user.getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // User saved successfully
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    public void updateCurrentUser() {
        userLiveData.postValue(firebaseAuth.getCurrentUser());
    }
}
