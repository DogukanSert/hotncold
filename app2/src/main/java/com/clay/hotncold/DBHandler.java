package com.clay.hotncold;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.clay.hotncold.group.Group;
import com.facebook.AccessToken;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DBHandler {

    private static void insertGroup(Group g)
    {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            mapper.save(g);
        } catch (AmazonServiceException ex) {
            Log.e("kaan", "Error inserting users");
            LoginActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }
    }

    public static void insertLatLong(UserLoc u)
    {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            mapper.save(u);
        } catch (AmazonServiceException ex) {
            Log.e("kaan", "Error inserting users");
            LoginActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }
    }

    public static void insertUser(User u) {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            mapper.save(u);
        } catch (AmazonServiceException ex) {
            Log.e("kaan", "Error inserting users");
            LoginActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }
    }

    public static void insertFriendship(Friendship f) {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            mapper.save(f);
        } catch (AmazonServiceException ex) {
            Log.e("kaan", "Error inserting users");
            LoginActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }
    }

    private static void addMeToFriendsAsync(String his)
    {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        String id=AccessToken.getCurrentAccessToken().getUserId();
        Friendship hisriendship = mapper.load(Friendship.class, his);

        if(hisriendship == null)
            Log.d("addFriend", his);

        String hisFriends = hisriendship.getMyFriends();

        hisFriends += id + "-";

        hisriendship.setMyFriends(hisFriends);
        mapper.save(hisriendship);
    }

    public static void addMeToFriends(String his)
    {
        Log.d("elif", "kaaan");
        new AddMeToFriend().execute(his);
        Log.d("elif", "kaaan");
    }

    private static void deleteFriendsAsync(ArrayList<String> del)
    {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        String id=AccessToken.getCurrentAccessToken().getUserId();
        Friendship myFriendship = mapper.load(Friendship.class, id);

        Log.d("elif", "gorki");
        String myFriends = myFriendship.getMyFriends();

        String friends[]=myFriends.split("-");

        ArrayList<String> newFriends = new ArrayList<>();

        for(String s : friends)
            newFriends.add(s);

        if(del!=null) {
            for (String s : del) {
                String delId;
                String part[]=s.split("-");
                delId = part[1];
                newFriends.remove(delId);
            }
        }

        String newVersion = "";

        for(int i=0; i<newFriends.size(); i++)
        {
            newVersion += newFriends.get(i) + "-";
            Log.d("kaan", newFriends.get(i));
        }
        myFriendship.setMyFriends(newVersion);
        mapper.save(myFriendship);
    }


    public static void deleteFriends(ArrayList<String> del)
    {
        Log.d("elif", "kaaan");
        new DeleteFriends().execute(del);
        Log.d("elif", "kaaan");
    }

    public static void updateLatLong(UserLoc u)
    {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            mapper.save(u);
        } catch (AmazonServiceException ex) {
            Log.e("kaan", "Error inserting users");
            LoginActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }
    }
    private static Group getGroupAsync(String name)
    {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        Group u = mapper.load(Group.class, name + "-" + AccessToken.getCurrentAccessToken().getUserId());

        return u;
    }

    public static Group getGroup(String name) {
        DBHandler.GetGroup x = new DBHandler.GetGroup();
        x.execute(name);
        Group us = null;
        try {
            us = x.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return us;
    }

    private static UserLoc getFriendLocAsync(String id)
    {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        UserLoc u = mapper.load(UserLoc.class, id);

        return u;
    }

    public static UserLoc getFriendLoc(String id)
    {
        DBHandler.GetUserLoc x = new DBHandler.GetUserLoc();
        x.execute(id);
        UserLoc us = null;
        try {
            us = x.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return us;
    }

    private static ArrayList<String> getFriendIdsAsync(String me)
    {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        Friendship u = mapper.load(Friendship.class, me);

        if(u==null)
            return null;

        String string = u.getMyFriends();
        String[] parts = string.split("-");


        ArrayList<String> friendIds=new ArrayList<>();
        for(int i=0; i<parts.length; i++)
            friendIds.add(parts[i]);

        return friendIds;
    }


    public static ArrayList<String> getFriendIds(String me)
    {
        ArrayList<String> friends = new ArrayList<>();

        DBHandler.GetFriendIds a = new DBHandler.GetFriendIds();
        a.execute(me);
        try {
            friends = a.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public ArrayList<User> getFriends(String me)
    {
        ArrayList<String> f = getFriendIds(me);
        Log.d("kaan", "11");

        ArrayList<User> friends = new ArrayList<>();
        Log.d("kaan", "12");
        for(int i=0; i<f.size();i++)
            friends.add(getUser(f.get(i)));
        Log.d("kaan", "13");

        return friends;
    }

    private static User getUserAsync(String id)
    {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        User u = mapper.load(User.class, id);

        if(u==null)
            return null;
        Log.d("ckaan", u.toString());

        return u;
    }

    public static User getUser(String id)
    {
        User us;
        DBHandler.GetUser x = new DBHandler.GetUser();
        x.execute(id);
        us = null;
        try {
            us = x.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return us;
    }

    private static ArrayList<Group> getMyGroupsAsync()
    {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        try {
            PaginatedScanList<Group> result = mapper.scan(
                    Group.class, scanExpression);

            ArrayList<Group> resultList = new ArrayList<>();
            for (Group up : result) {
                String[] parts = up.getGroupName().split("-");
                if(parts[1].equals(AccessToken.getCurrentAccessToken().getUserId()))
                    resultList.add(up);
            }
            return resultList;

        } catch (AmazonServiceException ex) {
            LoginActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return null;
    }

    public static ArrayList<Group> getMyGroups()
    {
        ArrayList<Group> groups = new ArrayList<>();
        DBHandler.GetGroups x = new DBHandler.GetGroups();
        x.execute();
        groups = null;
        try {
            groups = x.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d("kaan", e.toString());
        }
        return groups;
    }

    private static ArrayList<User> getAllUsersAsync()
    {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        try {
            PaginatedScanList<User> result = mapper.scan(
                    User.class, scanExpression);

            ArrayList<User> resultList = new ArrayList<User>();
            for (User up : result) {
                resultList.add(up);
            }
            return resultList;

        } catch (AmazonServiceException ex) {
            LoginActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return null;
    }

    public static ArrayList<User> getAllUsers()
    {
        ArrayList<User> userObjects;
        DBHandler.GetUsers x = new DBHandler.GetUsers();
        x.execute();
        userObjects = null;
        try {
            userObjects = x.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return userObjects;
    }

    public ArrayList<String> getAllUserIDs(){

        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        try {
            PaginatedScanList<User> result = mapper.scan(
                    User.class, scanExpression);

            ArrayList<String> resultList = new ArrayList<String>();
            for (User up : result) {
                resultList.add(up.getFacebookID());
            }
            return resultList;

        } catch (AmazonServiceException ex) {
            LoginActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return null;
    }

    public static void groupInsert(Group g)
    {
        new DBHandler.GroupAdd().execute(g);
    }

    public static void userInsert(User u)
    {
        new DBHandler.UserAdd().execute(u);
    }

    public static void friendshipInsert(Friendship f) {
        new DBHandler.FriendshipAdd().execute(f);
    }

    public void createGroup(ArrayList<String> users, String id, String groupName)
    {

    }

    private static class GetGroup extends
            AsyncTask<String, Void, Group> {

        protected void onPreExecute() {
        }

        @Override
        protected Group doInBackground(String... params) {
            return DBHandler.getGroupAsync(params[0]);
        }

        @Override
        protected void onPostExecute(Group aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static class GetUser extends
            AsyncTask<String, Void, User> {

        public GetUser() {
        }

        protected void onPreExecute() {
        }

        @Override
        protected User doInBackground(String... params) {
            return DBHandler.getUserAsync(params[0]);
        }

        @Override
        protected void onPostExecute(User aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static class GetFriendIds extends
            AsyncTask<String, Void, ArrayList<String>> {

        protected void onPreExecute() {
        }

        protected ArrayList<String> doInBackground(String... f) {
            return DBHandler.getFriendIdsAsync(f[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<String> aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static class GetGroups extends
            AsyncTask<Void, Void, ArrayList<Group>> {

        protected void onPreExecute() {
        }

        protected ArrayList<Group> doInBackground(Void... f) {
            return DBHandler.getMyGroupsAsync();
        }

        @Override
        protected void onPostExecute(ArrayList<Group> aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static class GetUsers extends
            AsyncTask<Void, Void, ArrayList<User>> {

        public GetUsers() {
        }

        protected void onPreExecute() {
        }

        protected ArrayList<User> doInBackground(Void... f) {
            return DBHandler.getAllUsersAsync();
        }

        @Override
        protected void onPostExecute(ArrayList<User> aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static class GetUserLoc extends
            AsyncTask<String, Void, UserLoc> {

        public GetUserLoc() {
        }

        protected void onPreExecute() {
        }

        protected UserLoc doInBackground(String... f) {
            return DBHandler.getFriendLocAsync(f[0]);
        }

        @Override
        protected void onPostExecute(UserLoc aVoid) {
            super.onPostExecute(aVoid);
            //dialog.dismiss();
        }
    }

    private static class GroupAdd extends
            AsyncTask<Group, Void, Void> {

        protected Void doInBackground(Group... u) {
            DBHandler.insertGroup(u[0]);
            return null;
        }
    }

    private static class UserAdd extends
            AsyncTask<User, Void, Void> {

        protected Void doInBackground(User... u) {
            DBHandler.insertUser(u[0]);
            return null;
        }
    }

    private static class FriendshipAdd extends
            AsyncTask<Friendship, Void, Void> {

        protected Void doInBackground(Friendship... f) {
            DBHandler.insertFriendship(f[0]);
            return null;
        }
    }

    private static class DeleteFriends extends
            AsyncTask<ArrayList<String>, Void, Void> {

        protected Void doInBackground(ArrayList<String>... u) {
            Log.d("elif", "kdog");
            DBHandler.deleteFriendsAsync(u[0]);
            Log.d("elif", "mira");
            return null;
        }
    }

    private static class AddMeToFriend extends
            AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            DBHandler.addMeToFriendsAsync(params[0]);
            return null;
        }
    }


}


