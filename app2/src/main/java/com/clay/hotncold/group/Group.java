package com.clay.hotncold.group;

import java.util.ArrayList;

/**
 * Created by elif on 13.4.2016.
 */
public class Group {

    public String groupName;
    public ArrayList<String> mygroupFriends;
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

    public String getGroupName() {
        return groupName;
    }

    public String getMyFriends() {
        return mygroupFriends.get(1);
    }

    public ArrayList<String> getMygroupFriends() {
        return mygroupFriends;
    }

    public void addFriendstoGroup(String friendname)
    {
        mygroupFriends.add(friendname);
    }

    public int getgroupMemberNumber()
    {
        return mygroupFriends.size();
    }






}
