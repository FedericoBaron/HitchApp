package com.example.hitchapp.models;

import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Parcel(analyze={User.class})
@ParseClassName("_User")
public class User extends ParseUser {

    // Define all the user keys
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSOWRD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    public static final String KEY_BIOGRAPHY = "biography";
    public static final String KEY_COLLEGE = "college";
    public static final String KEY_COLLEGE_ID = "collegeId";
    public static final String KEY_DRIVERS_LICENSE = "driversLicense";
    public static final String KEY_DRIVER_TYPE = "driverType";
    public static final String KEY_CAR = "car";
    public static final String KEY_REVIEWS = "reviews";
    public static final String KEY_IS_DRIVER = "isDriver";

    //Fields must be public for parceler
    String objectId;
    String firstName;
    String lastName;
    String username;
    ParseFile profilePicture;

    // no-arg, empty constructor required for parceler
    public User(){}

    public User (JSONObject jsonObject) throws JSONException {
        objectId = jsonObject.getString("objectId");
        Log.i("MODEL", jsonObject.toString());
        firstName = jsonObject.getString("firstName");
        lastName = jsonObject.getString("lastName");
        username = jsonObject.getString("username");
        profilePicture = (ParseFile) jsonObject.get("profilePicture");
    }

    public static List<User> fromJsonArray(JSONArray userJsonArray) throws JSONException {
        List<User> users = new ArrayList<>();
        for(int i = 0; i < userJsonArray.length(); i++) {
            users.add(new User(userJsonArray.getJSONObject(i)));
        }
        return users;
    }

    public String getFirstName(){return getString(KEY_FIRST_NAME);}

    public void setFirstName(String firstName){put(KEY_FIRST_NAME, firstName);}

    public String getLastName(){return getString(KEY_LAST_NAME);}

    public void setLastName(String lastName){put(KEY_LAST_NAME, lastName);}

    public ParseFile getProfilePicture(){return getParseFile(KEY_PROFILE_PICTURE);}

    public void setProfilePicture(ParseFile profilePicture){put(KEY_PROFILE_PICTURE, profilePicture);}

    public String getBiography(){return getString(KEY_BIOGRAPHY);}

    public void setBiography(String biography){put(KEY_BIOGRAPHY, biography);}

    public String getCollege(){return getString(KEY_COLLEGE);}

    public void setCollege(String college){put(KEY_COLLEGE, college);}

    public ParseFile getCollegeId(){return getParseFile(KEY_COLLEGE_ID);}

    public void setCollegeId(ParseFile collegeId){put(KEY_COLLEGE_ID, collegeId);}

    public ParseFile getDriversLicense(){return getParseFile(KEY_DRIVERS_LICENSE);}

    public void setDriversLicense(String driversLicense){put(KEY_DRIVERS_LICENSE, driversLicense);}

    public JSONArray getDriverType(){return getJSONArray(KEY_DRIVER_TYPE);}

    public void setDriverType(JSONArray driverType){put(KEY_DRIVER_TYPE, driverType);}

    public Boolean getIsDriver(){return getBoolean(KEY_IS_DRIVER);}

    public void setIsDriver(boolean isDriver){put(KEY_IS_DRIVER, isDriver);}

    public Car getCar(){return (Car) getParseObject(KEY_CAR);}

    public void setCar(Car car){put(KEY_CAR, car);}

    public JSONArray getReviews(){return getJSONArray(KEY_REVIEWS);}

    public void setReviews(JSONArray reviews){put(KEY_REVIEWS, reviews);}
}
