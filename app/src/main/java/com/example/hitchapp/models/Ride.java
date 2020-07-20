package com.example.hitchapp.models;

import org.json.JSONArray;
import org.parceler.Parcel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@Parcel(analyze={Ride.class})
@ParseClassName("Ride")
public class Ride extends ParseObject{

    public static final String KEY_DRIVER = "driver";
    public static final String KEY_FROM = "from";
    public static final String KEY_TO = "to";
    public static final String KEY_DEPARTURE_TIME = "departureTime";
    public static final String KEY_PARTICIPANTS = "participants";
    public static final String KEY_PRICE = "price";
    public static final String KEY_PRICE_PER_PARTICIPANT = "pricePerParticipant";
    public static final String KEY_SEATS_AVAILABLE = "seatsAvailable";
    public static final String KEY_DEPARTURE_DATE = "departureDate";
    public static final String KEY_MESSAGES = "messages";
    public static final String KEY_REQUESTS = "requests";


    public ParseUser getDriver(){ return getParseUser(KEY_DRIVER); }

    public void setDriver(ParseUser user){
        put(KEY_DRIVER, user);
    }

    public JSONArray getParticipants(){return getJSONArray(KEY_PARTICIPANTS);}

    public void setParticipants(JSONArray array){put(KEY_PARTICIPANTS,array);}

    public JSONArray getRequests(){return getJSONArray(KEY_REQUESTS);}

    public void setRequests(JSONArray array){put(KEY_REQUESTS,array);}

    public String getFrom(){return getString(KEY_FROM);}

    public void setFrom(String from){put(KEY_FROM, from);}

    public String getTo(){return getString(KEY_TO);}

    public void setTo(String to){put(KEY_TO, to);}

    public String getDepartureTime(){return getString(KEY_DEPARTURE_TIME);}

    public void setDepartureTime(String time){put(KEY_DEPARTURE_TIME, time);}

    public String getDepartureDate(){return getString(KEY_DEPARTURE_DATE);}

    public void setDepartureDate(String time){put(KEY_DEPARTURE_DATE, time);}

    public int getPrice(){return (int) getNumber(KEY_PRICE);}

    public void setPrice(int price){put(KEY_PRICE, price);}

    public boolean getPricePerParticipant(){return getBoolean(KEY_PRICE_PER_PARTICIPANT);}

    public void setPricePerParticipant(boolean perParticipant){put(KEY_PRICE_PER_PARTICIPANT, perParticipant);}

    public int getSeatsAvailable(){return (int) getNumber(KEY_SEATS_AVAILABLE);}

    public void setSeatsAvailable(int seatsAvailable){put(KEY_SEATS_AVAILABLE, seatsAvailable);}

    public JSONArray getMessages() {return getJSONArray(KEY_MESSAGES);}

    public void setMessages(JSONArray messages){put(KEY_MESSAGES, messages);}
}