package com.example.hitchapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.Date;

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

    // Fields must be public for parceler
//    String objectId;
//    String
//
//    public static User fromJson(JSONObject jsonObject) {
//        User user = new User();
//        // Deserialize json into object fields
//        try {
//            user.objectId = jsonObject.getString("id");
//            b.name = jsonObject.getString("name");
//            b.phone = jsonObject.getString("display_phone");
//            b.imageUrl = jsonObject.getString("image_url");
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }
//        // Return new object
//        return b;
//    }

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
