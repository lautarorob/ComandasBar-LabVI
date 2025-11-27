package com.example.comandasbar.api.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("email")
    private String email;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    // Getters para poder leer los datos después y guardarlos en Room
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}