package com.clay.hotncold;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by ckaan on 12.4.2016.
 */
@DynamoDBTable(tableName = "friendship")
public class Friendship {
    private String id;
    private String myFriends;

    @DynamoDBHashKey(attributeName = "id")
    public String getMe() {
        return id;
    }

    public void setMe(String me) {
        this.id = me;
    }

    @DynamoDBAttribute(attributeName = "friends")
    public String getMyFriends() {
        return myFriends;
    }

    public void setMyFriends(String myFriends) {
        this.myFriends = myFriends;
    }
}
