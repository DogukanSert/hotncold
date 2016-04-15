package com.clay.hotncold;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.firebase.client.Firebase;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DBHandler {

    Firebase ref;
    String friendIDsOfMe;
    String usersObject;
    String locationObjects;

    public DBHandler() {
        /*ref = new Firebase("https://hotncold2.firebaseio.com/");
        friendIDsOfMe=getFriendsDB(AccessToken.getCurrentAccessToken().getUserId());
        usersObject=getUsersDB();
        locationObjects=getLocationsDB();*/
    }

    /*public void setFriendIDsOfMe(String s)
    {
        friendIDsOfMe = s;
    }*/

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

    /*public String getFriendsDB(String me)
    {
        DBHandler.HttpAsyncTask x =new DBHandler.HttpAsyncTask();
        x.execute("https://hotncold2.firebaseio.com/users/"+me+"/friends.json");
        String s = null;
        try {
            s = x.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return s;
    }*/

    /*public String getUsersDB()
    {
        DBHandler.HttpAsyncTask x =new DBHandler.HttpAsyncTask();
        x.execute("https://hotncold2.firebaseio.com/users.json");
        String s = null;
        try {
            s = x.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return s;
    }*/

    /*public String getLocationsDB()
    {
        DBHandler.HttpAsyncTask x =new DBHandler.HttpAsyncTask();
        x.execute("https://hotncold2.firebaseio.com/location.json");
        String s = null;
        try {
            s = x.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return s;
    }*/

   /* public void addUserToDb(User user,String s)
    {
        Firebase alanRef = ref.child("users").child(s);
        alanRef.setValue(user);
    }*/

    /*public void updateLatLong(String s, Location l)
    {
        if(l!=null) {
            Firebase alanRef = ref.child("location").child(s);
            UserLoc loc = new UserLoc(l.getLatitude(), l.getLongitude(), l.getSpeed(), l.getTime());
            alanRef.setValue(loc);
        }
        else{
            Firebase alanRef = ref.child("location").child(s);
            UserLoc loc = new UserLoc(0,0,0,0);
            alanRef.setValue(loc);
        }
    }

    public void addFriendToDB(String me, String friend)
    {
        Firebase alanRef = ref.child("users").child(me).child("friends");
        alanRef.push().setValue(friend);
    }

    public boolean isUserNull(String me)
    {
        DBHandler.HttpAsyncTask x =new DBHandler.HttpAsyncTask();
        x.execute("https://hotncold2.firebaseio.com/users/" + me + ".json");
        String s = null;
        try {
            s = x.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if(s==null || s.equals("null"))
            return true;
        else
            return false;
    }

    public boolean isFriendsNull(String me)
    {
        DBHandler.HttpAsyncTask x =new DBHandler.HttpAsyncTask();
        x.execute("https://hotncold2.firebaseio.com/users/" + me + "/friends.json");
        String s = null;
        try {
            s = x.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d("kaan", e.toString());
        }
        finally {

            if(s==null || s.equals("null")) {
                Log.d("kaan","if"+ s);
                return true;
            }
            else
                return false;
        }
    }*/

    public UserLoc getFriendLoc(String id)
    {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        UserLoc u = mapper.load(UserLoc.class, id);

        return u;


        /*JSONObject json = null;

        try {
            json = new JSONObject(locationObjects);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            json = json.getJSONObject(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserLoc u;

        u=new UserLoc(Double.parseDouble(json.optString("lat")),
                Double.parseDouble(json.optString("lon")),
                Float.parseFloat(json.optString("speed")),
                Long.parseLong(json.optString("time")));

        return u;*/

        /*DBHandler.HttpAsyncTask x =new DBHandler.HttpAsyncTask();
        x.execute("https://hotncold2.firebaseio.com/location/" + id + ".json");
        String s = null;
        try {
            s = x.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d("kaan", e.toString());
        }

        Log.d("kaan", s);
        UserLoc u=null;

        JSONObject json;

        try {
            json = new JSONObject(s);

            u=new UserLoc(Double.parseDouble(json.optString("lat")),
                    Double.parseDouble(json.optString("lon")),
                    Float.parseFloat(json.optString("speed")),
                    Long.parseLong(json.optString("time")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;*/
    }



    public ArrayList<String> getFriendIds(String me)
    {

        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        Friendship u = mapper.load(Friendship.class, me);

        String string = u.getMyFriends();
        String[] parts = string.split("-");

        ArrayList<String> friendIds=new ArrayList<>();
        for(int i=0; i<parts.length; i++)
            friendIds.add(parts[i]);

        return friendIds;


        /*JSONObject json = null;
        JSONArray arr=null;

        try {
            json = new JSONObject(friendIDsOfMe);
            arr = json.names();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<String> friends = new ArrayList<>();

        for(int i=0; i<arr.length(); i++)
        {
            try {
                assert json != null;
                friends.add(json.getString(arr.get(i).toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return friends;*/

        /*int i=0;
        while(!json.isNull("friend"+i))
        {
            try {
                friends.add(json.getString("friend"+i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i++;
        }

        return friends;*/

        /*DBHandler.HttpAsyncTask x =new DBHandler.HttpAsyncTask();
        x.execute("https://hotncold2.firebaseio.com/users/"+me+"/friends.json");
        String s = null;
        try {
            s = x.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        JSONObject json;
        ArrayList<String> list=null;

        try {
            json = new JSONObject(s);

            list = new ArrayList<>();
            for(int i = 0 ; i < json.length() ; i++) {
                list.add(json.getString("friend" + i));
                //Log.d("kaan", list.get(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;*/
    }

    public ArrayList<User> getFriends(String me)
    {
        /*DBHandler.HttpAsyncTask x =new DBHandler.HttpAsyncTask();
        x.execute("https://hotncold2.firebaseio.com/users/" + me + "/friends.json");
        String s = null;
        try {
            s = x.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        JSONObject json;
        ArrayList<String> list=null;

        try {
            json = new JSONObject(s);

            list = new ArrayList<>();
            for(int i = 0 ; i < json.length() ; i++) {
                list.add(json.getString("friend" + i));
                //Log.d("kaan", list.get(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        Log.d("kaan", "10");

        ArrayList<String> f = getFriendIds(me);
        Log.d("kaan", "11");

        ArrayList<User> friends = new ArrayList<>();
        Log.d("kaan", "12");
        for(int i=0; i<f.size();i++)
            friends.add(getUser(f.get(i)));
        Log.d("kaan", "13");

        return friends;
        /*ArrayList<User> u=null;
        try {
           u  = s.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return u;*/

        //Log.d("kaan", s);
    }

    public ArrayList<String> getFriendsNames(String id)
    {
        JSONObject json = null;
        JSONArray arr=null;

        try {
            json = new JSONObject(friendIDsOfMe);
            arr = json.names();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<String> friends = new ArrayList<>();

        for(int i=0; i<arr.length(); i++)
        {
            try {
                assert json != null;
                friends.add(json.getString(arr.get(i).toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return friends;
    }

    public static User getUser(String id)
    {
        AmazonDynamoDBClient ddb = LoginActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        User u = mapper.load(User.class, id);

        Log.d("ckaan", u.toString());

        return u;
        /*String s = usersObject;

        User u = new User();

        JSONObject json1 = null;

        try {
            json1 = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject json = json1.getJSONObject(id);
            u.setUsername(json.optString("username"));
            u.setSurname(json.optString("surname"));
            //u.setBirthday(json.optString("birthday"));
            //u.setEmail(json.optString("email"));
            u.setFacebookID(json.optString("facebookID"));
            //u.setGender(json.optString("gender"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("kaan", "bitti");
        return u;*/
    }

    public static ArrayList<User> getAllUsers()
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

        /*ArrayList<String> userIds = getAllUserIDs();
        ArrayList<User> users=new ArrayList<>();

        for(String s : userIds)
            users.add(getUser(s));

        return users;*/
        /*Firebase re;

        re = new Firebase("https://hotncold2.firebaseio.com/users");
        Log.d("kaan", "getAllUsers");

        re.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    User u = postSnapshot.getValue(User.class);
                    Log.d("kaan", u.getUsername() + " - " + u.getSurname());
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        return null;*/
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



        /*String s= usersObject;

        JSONObject json;
        JSONArray arr=null;

        try {
            json = new JSONObject(s);
            arr = json.names();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<String> users=new ArrayList<>();

        try {
            for(int i=0; i<arr.length(); i++) {
                users.add(arr.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;*/

        /*DBHandler.HttpAsyncTask x =new DBHandler.HttpAsyncTask();
        x.execute("https://hotncold2.firebaseio.com/users.json");

        String s = null;
        try {
            s = x.get();

        } catch (InterruptedException | ExecutionException e) {
            Log.d("kaan", e.toString());
        }

        JSONObject json;
        JSONArray arr=null;

        try {
            json = new JSONObject(s);
            arr = json.names();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<String> users=new ArrayList<>();

        try {
            for(int i=0; i<arr.length(); i++) {
                users.add(arr.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;*/
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("kaan", e.toString());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    /*public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }*/

    public static class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Log.d("kaan", result);
        }

    }

    /*public class GetFriends extends AsyncTask<String, Void, ArrayList<User>>{
        @Override
        protected ArrayList<User> doInBackground(String... params) {
            Log.d("kaan", "10");

            ArrayList<String> f = getFriendIds(params[0]);
            Log.d("kaan", "11");

            ArrayList<User> friends = new ArrayList<>();
            Log.d("kaan", "12");
            for(int i=0; i<f.size();i++)
                friends.add(getUser(f.get(i)));
            Log.d("kaan", "13");

            return friends;
        }

        @Override
        protected void onPostExecute(ArrayList<User> st) {
            super.onPostExecute(st);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }*/

    /*public ArrayList<String> getFriends(final String me)
    {
        Firebase alanRef = ref.child("users");

        alanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<String> friends;
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    User post = userSnapshot.getValue(User.class);
                    if(post.getUserID().equals(me)) {
                        friends = post.getFriends();
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        return friends;
    }*/
}


