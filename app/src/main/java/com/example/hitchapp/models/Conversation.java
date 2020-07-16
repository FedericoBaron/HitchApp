package com.example.hitchapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.parceler.Parcel;

@Parcel(analyze={Conversation.class})
@ParseClassName("Conversation")
public class Conversation extends ParseObject {

    public static final String KEY_PARTICIPANTS = "participants";
    public static final String KEY_MESSAGES = "messages";

    public JSONArray getParticipants(){return getJSONArray(KEY_PARTICIPANTS);}

    public void setParticipants(JSONArray participants){put(KEY_PARTICIPANTS, participants);}

    public JSONArray getMessages(){return getJSONArray(KEY_MESSAGES);}

    public void setMessages(JSONArray messages){put(KEY_MESSAGES, messages);}


}
