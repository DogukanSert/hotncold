package com.clay.hotncold;

import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Created by elif on 20.2.2016.
 */

@DynamoDBTable(tableName = "users")
public class User implements ClusterItem {

    public String username;
    public String surname;
    public String gender;
    public String birthday;
    public String email;
    public String facebookID;
    public String beaconID;

    //Profile p;
    //private ProfileTracker mProfileTracker;

    public User(JSONObject object)
    {
        try{
            facebookID = object.getString("id");
            email = object.optString("email");
            gender = object.optString("gender");
            birthday = object.optString("birthday");
            username=object.optString("first_name");
            surname=object.optString("last_name");
            beaconID=toHex(facebookID);
        } catch(JSONException e){
            Log.d("kaan", e.toString());
            e.printStackTrace();
        }
    }

    public User(){}


    /*public void setProfile()
    {
        /*if(Profile.getCurrentProfile() == null) {
            mProfileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                    // profile2 is the new profile
                    Log.v("facebook - profile", profile2.getFirstName());
                    mProfileTracker.stopTracking();
                }
            };
            mProfileTracker.startTracking();
        }

        p=Profile.getCurrentProfile();

        while(p == null) ;

        p=Profile.getCurrentProfile();
    }*/

    //SET GET METHODS
    @DynamoDBAttribute(attributeName = "birthday")
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @DynamoDBAttribute(attributeName = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute(attributeName = "gender")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @DynamoDBAttribute(attributeName = "surname")
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @DynamoDBAttribute(attributeName = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDBHashKey(attributeName = "id")
    public String getFacebookID() {
        return facebookID;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    @DynamoDBAttribute(attributeName = "beacon")
    public String getBeaconID() {
        return beaconID;
    }

    public void setBeaconID(String beaconID) {
        this.beaconID = beaconID;
    }

    public static String toHex(String base)
    {
        BigInteger i = new BigInteger(base);
        String result = i.toString(16);
        while(result.length()<16)
            result= "0"+result;

        return result;
    }

    @Override
    public LatLng getPosition() {
        UserLoc u = DBHandler.getFriendLoc(getFacebookID());
        return new LatLng(u.getLat(), u.getLon());
    }
}



