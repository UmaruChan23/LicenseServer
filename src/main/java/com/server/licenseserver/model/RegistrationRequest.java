package com.server.licenseserver.model;

import javax.validation.constraints.NotEmpty;

public class RegistrationRequest {

    @NotEmpty
    private String login;

    @NotEmpty
    private String password;

    public RegistrationRequest() {
    }

    public RegistrationRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
