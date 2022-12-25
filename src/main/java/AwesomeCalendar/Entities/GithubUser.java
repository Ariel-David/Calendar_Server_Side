package AwesomeCalendar.Entities;

import javax.persistence.*;

import static AwesomeCalendar.Utilities.Utility.encryptPassword;

public class GithubUser {
    private String name;
    private String username;
    private String password;
    private String accessToken;

    public static GithubUser registeredGithubUser(GithubUser gitHubUser) {
        GithubUser currUser = new GithubUser();
        currUser.setPassword(encryptPassword(gitHubUser.getPassword()));
        return currUser;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

