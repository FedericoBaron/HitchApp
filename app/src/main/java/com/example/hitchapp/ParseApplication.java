package com.example.hitchapp;

import android.app.Application;

import com.example.hitchapp.models.Car;
import com.example.hitchapp.models.Message;
import com.example.hitchapp.models.Request;
import com.example.hitchapp.models.Ride;
//import com.example.hitchapp.models.User;
import com.example.hitchapp.models.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication extends Application{

    @Override
    public void onCreate(){
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(Ride.class);
        ParseUser.registerSubclass(User.class);
        ParseObject.registerSubclass(Car.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Request.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("federico-hitchapp") // should correspond to APP_ID env variable
                .clientKey("CodepathMoveFastParse")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://federico-hitchapp.herokuapp.com/parse").build());
    }
}
