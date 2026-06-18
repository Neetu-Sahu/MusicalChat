package com.example.musicalchat.data.model;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String roomId;
    private String name;
    private String hostUid;
    private long createdAt;
    private List<String> memberUids;
    
    @PropertyName("public")
    private boolean isPublic;

    public Room() {
        this.memberUids = new ArrayList<>();
    }

    public Room(String roomId, String name, String hostUid, boolean isPublic) {
        this.roomId = roomId;
        this.name = name;
        this.hostUid = hostUid;
        this.isPublic = isPublic;
        this.createdAt = System.currentTimeMillis();
        this.memberUids = new ArrayList<>();
        this.memberUids.add(hostUid);
    }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHostUid() { return hostUid; }
    public void setHostUid(String hostUid) { this.hostUid = hostUid; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public List<String> getMemberUids() { return memberUids; }
    public void setMemberUids(List<String> memberUids) { this.memberUids = memberUids; }

    @PropertyName("public")
    public boolean isPublic() { return isPublic; }
    
    @PropertyName("public")
    public void setPublic(boolean aPublic) { isPublic = aPublic; }
}
