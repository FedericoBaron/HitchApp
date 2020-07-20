package com.example.hitchapp.models;

import org.json.JSONArray;
import org.parceler.Parcel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@Parcel(analyze={Request.class})
@ParseClassName("Request")
public class Request extends ParseObject{

    public static final String KEY_RIDE = "ride";
    public static final String KEY_REQUESTER = "requester";
    public static final String KEY_DRIVER = "driver";


    public ParseUser getRequester(){ return getParseUser(KEY_REQUESTER); }

    public void setRequester(ParseUser user){
        put(KEY_REQUESTER, user);
    }

    public Ride getRide(){ return (Ride) getParseObject(KEY_RIDE);}

    public void setRide(Ride ride){ put(KEY_RIDE, ride);}

    public ParseUser getDriver(){ return getParseUser(KEY_DRIVER); }

    public void setDriver(ParseUser driver) { put(KEY_DRIVER, driver);} {
    }
}