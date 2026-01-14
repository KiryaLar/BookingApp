package ru.larkin.bookingservice.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterUserRequest {

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 8)
    private String password;

    @NotNull
    @Size(min = 1)
    private String name;

    public RegisterUserRequest() {
    }

    public RegisterUserRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

