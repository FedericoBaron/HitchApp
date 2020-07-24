package com.example.hitchapp.repositories;

import com.parse.LogInCallback;
import com.parse.ParseUser;

public class UserRepository {

    public static final String TAG = "UserRepository";
    private static UserRepository instance;

    public static UserRepository getInstance(){
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }

    public void loginUser(String username, String password, LogInCallback loginCallBack){
        ParseUser.logInInBackground(username, password, loginCallBack);
    }

    public void signupUser(String username, String password, LogInCallback loginCallBack){

    }
}
