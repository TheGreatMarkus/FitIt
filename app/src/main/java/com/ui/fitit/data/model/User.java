package com.ui.fitit.data.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor

public class User implements Serializable {
    String username;
    String hashedPassword;
    String fullName;
}
