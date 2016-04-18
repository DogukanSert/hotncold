package com.clay.hotncold.group;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.ArrayList;

/**
 * Created by elif on 13.4.2016.
 */
@DynamoDBTable(tableName = "groups")
public class Group {

    private String groupName;
    private ArrayList<String> mygroupFriends;
    private String friendIds;
    int iconID;

    public Group(String name)
    {
        groupName = name;
        mygroupFriends = new ArrayList<>();
    }
    public Group()
    {
        groupName = "default";
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setMygroupFriends(ArrayList<String> mygroupFriends) {
        this.mygroupFriends = mygroupFriends;
    }

    @DynamoDBHashKey(attributeName = "groupname")
    public String getGroupName() {
        return groupName;
    }

    public ArrayList<String> getMygroupFriends() {
        return mygroupFriends;
    }

    public void addFriendstoGroup(String friendname)
    {
        mygroupFriends.add(friendname);
    }


    @DynamoDBAttribute(attributeName = "friends")
    public String getMyFriends() {
        return friendIds;
    }

    public void setMyFriends(String friendIds) {
        this.friendIds = friendIds;
        ArrayList<String> myGroup = new ArrayList<>();

        String[] parts = friendIds.split("-");
        for(int i=0; i<parts.length; i++)
            myGroup.add(parts[i]);
        setMygroupFriends(myGroup);
    }
}
