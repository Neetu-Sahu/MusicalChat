package com.example.musicalchat.data.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class RoomMeta {
    public String room_name;
    public String host_id;
    public boolean is_private;
    public long created_at;
    public boolean needs_new_host;
    public long host_disconnected_at;

    public RoomMeta() {
    }

    public RoomMeta(String room_name, String host_id, boolean is_private, long created_at) {
        this.room_name = room_name;
        this.host_id = host_id;
        this.is_private = is_private;
        this.created_at = created_at;
        this.needs_new_host = false;
        this.host_disconnected_at = 0;
    }
}
