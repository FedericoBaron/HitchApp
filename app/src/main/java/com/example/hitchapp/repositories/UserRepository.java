package com.example.hitchapp.repositories;

import com.example.hitchapp.models.User;
import com.parse.LogInCallback;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

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

    public void signupUser(User user, SignUpCallback signUpCallback){
        user.signUpInBackground(signUpCallback);
    }

    public void savePhoto(ParseFile photo, SaveCallback saveCallback){
        photo.saveInBackground(saveCallback);
    }
}
