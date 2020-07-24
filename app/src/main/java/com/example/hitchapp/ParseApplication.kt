package com.example.hitchapp

import android.app.Application
import com.example.hitchapp.models.*
import com.parse.Parse
import com.parse.ParseObject
import com.parse.ParseUser

//import com.example.hitchapp.models.User;
class ParseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Register your parse models
        ParseObject.registerSubclass(Ride::class.java)
        ParseUser.registerSubclass(User::class.java)
        ParseObject.registerSubclass(Car::class.java)
        ParseObject.registerSubclass(Message::class.java)
        ParseObject.registerSubclass(Request::class.java)

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(Parse.Configuration.Builder(this)
                .applicationId("federico-hitchapp") // should correspond to APP_ID env variable
                .clientKey("CodepathMoveFastParse") // set explicitly unless clientKey is explicitly configured on Parse server
                //.server("http://0.0.0.0:4040/parse").build());
                .server("http://federico-hitchapp.herokuapp.com/parse").build())
    }
}