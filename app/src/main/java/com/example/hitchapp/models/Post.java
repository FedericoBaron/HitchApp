package com.example.hitchapp.models;

import org.json.JSONArray;
import org.parceler.Parcel;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@Parcel(analyze={Post.class})
@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DRIVER = "driver";
    public static final String KEY_FROM = "from";
    public static final String KEY_TO = "to";
    public static final String KEY_DEPARTURE_TIME = "departureTime";
    public static final String KEY_ARRIVAL_TIME = "arrivalTime";
    public static final String KEY_PARTICIPANTS = "participants";
    public static final String KEY_PRICE = "price";
    public static final String KEY_PRICE_PER_PARTICIPANT = "pricePerParticipant";
    public static final String KEY_SEATS_AVAILABLE = "seatsAvailable";


    public ParseUser getDriver(){ return getParseUser("driver"); }

    public void setDriver(ParseUser user){
        put(KEY_DRIVER, user);
    }

    public JSONArray getParticipants(){return getJSONArray(KEY_PARTICIPANTS);}

    public void setParticipants(JSONArray array){put(KEY_PARTICIPANTS,array);}

    public String getFrom(){return getString(KEY_FROM);}

    public void setFrom(String from){put(KEY_FROM, from);}

    public String getTo(){return getString(KEY_TO);}

    public void setTo(String to){put(KEY_TO, to);}

    public Date getDepartureTime(){return getDate(KEY_DEPARTURE_TIME);}

    public void setDepartureTime(Date time){put(KEY_DEPARTURE_TIME, time);}

    public Date getArrivalTime(){return getDate(KEY_ARRIVAL_TIME);}

    public void setArrivalTime(String time){put(KEY_ARRIVAL_TIME, time);}

    public double getPrice(){return (double) getNumber(KEY_PRICE);}

    public void setPrice(double price){put(KEY_PRICE, price);}

    public boolean getPricePerParticipant(){return getBoolean(KEY_PRICE_PER_PARTICIPANT);}

    public void setPricePerParticipant(boolean perParticipant){put(KEY_PRICE_PER_PARTICIPANT, perParticipant);}

    public int getSeatsAvailable(){return (int) getNumber(KEY_SEATS_AVAILABLE);}

    public void setSeatsAvailable(int seatsAvailable){put(KEY_SEATS_AVAILABLE, seatsAvailable);}

}