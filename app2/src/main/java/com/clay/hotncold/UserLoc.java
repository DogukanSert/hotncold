package com.clay.hotncold;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by ckaan on 26.3.2016.
 */

@DynamoDBTable(tableName = "location")
public class UserLoc{
    public String id;
    public double lat, lon;
    public float speed;
    public long time;

    public UserLoc() {
    }

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "lat")
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @DynamoDBAttribute(attributeName = "lon")
    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @DynamoDBAttribute(attributeName = "speed")
    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @DynamoDBAttribute(attributeName = "time")
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public UserLoc(double lat, double lon, float speed, long time) {
        this.lat = lat;
        this.lon = lon;
        this.speed = speed;
        this.time = time;
    }
}