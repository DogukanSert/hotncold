package com.clay.hotncold;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager cbm;
    DBHandler dbHandler;

    public static AmazonClientManager clientManager = null;

    User user;
    User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        clientManager = new AmazonClientManager(this);

        newUser = new User();
        if(AccessToken.getCurrentAccessToken()!=null)
        {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }

        login();

    }

    public void login() {

        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        assert loginButton != null;
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "user_about_me", "user_birthday", "basic_info", "user_likes"));
        cbm = CallbackManager.Factory.create();
        loginButton.registerCallback(cbm, new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(final LoginResult loginResult) {
                Profile p;

                if (Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            if (profile2 != null) {
                                Toast.makeText(getApplicationContext(), profile2.getName() + " logged in.", Toast.LENGTH_LONG).show();
                                mProfileTracker.stopTracking();
                                Profile.setCurrentProfile(profile2);
                            }
                        }
                    };
                    mProfileTracker.startTracking();

                } else {
                    p = Profile.getCurrentProfile();
                    Toast.makeText(getApplicationContext(), p.getName() + " log in.", Toast.LENGTH_LONG).show();
                }


                dbHandler = new DBHandler();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        String id = null;
                        try {
                            id = object.getString("id");
                            Log.d("kaan", object.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        newUser = DBHandler.getUser(id);

                        user = new User(object);
                        UserLoc userLoc = new UserLoc(0, 0, 0, 0, 0);
                        userLoc.setId(id);
                        DBHandler.insertLatLong(userLoc);
                        DBHandler.userInsert(user);
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields",
                        "id,first_name,email,gender, birthday, last_name");
                request.setParameters(parameters);
                request.executeAsync();

                //get facebook friends which are using hotncold


                new GraphRequest(loginResult.getAccessToken(), "/me/friends", null, HttpMethod.GET, new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray friends = response.getJSONObject().getJSONArray("data");
                            ArrayList<String> myFriends = new ArrayList<String>();
                            String s = "";
                            for (int i = 0; i < friends.length(); i++) {
                                JSONObject rec = friends.getJSONObject(i);
                                s += rec.getString("id") + "-";
                                myFriends.add(rec.getString("id"));
                            }
                            Friendship f = new Friendship();
                            String id = loginResult.getAccessToken().getUserId();
                            f.setMe(id);
                            f.setMyFriends(s);

                            if (DBHandler.getFriendIds(id) == null) {
                                Log.d("kaan", "null geldi ekledi ");
                                DBHandler.friendshipInsert(f);
                                for (String user : myFriends) {
                                    DBHandler.addMeToFriends(user);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("elif", response.getJSONObject().toString());
                    }
                }
                ).executeAsync();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cbm.onActivityResult(requestCode, resultCode, data);

        if (cbm.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }
}
