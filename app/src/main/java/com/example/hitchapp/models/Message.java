package com.example.hitchapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze={Message.class})
@ParseClassName("Message")
public class Message extends ParseObject {

    public static final String KEY_CONTENT = "content";
    public static final String KEY_AUTHOR = "author";

    public String getContent(){return getString(KEY_CONTENT);}

    public void setContent(String content){put(KEY_CONTENT, content);}

    public ParseUser getAuthor(){ return getParseUser(KEY_AUTHOR); }

    public void setAuthor(ParseUser user){
        put(KEY_AUTHOR, user);
    }
}