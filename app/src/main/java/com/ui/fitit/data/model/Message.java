package com.ui.fitit.data.model;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    final String id = UUID.randomUUID().toString();
    long timestamp;
    String text;
    String sender;

    @Override
    @NonNull
    public String toString() {
        return String.format("%s: %s", sender, text);
    }

    public int compareTo(Message o) {
        return Instant.ofEpochMilli(o.timestamp).compareTo(Instant.ofEpochMilli(this.timestamp));
    }
}
