package com.clay.hotncold;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager cbm;
    DBHandler dbHandler;

    public static AmazonClientManager clientManager = null;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        clientManager = new AmazonClientManager(this);

        Button login_linkedin_btn = (Button) findViewById(R.id.LI_login_button);
        assert login_linkedin_btn != null;
        login_linkedin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_linkedin();
            }
        });

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
                        user = new User(object);
                        new UserAdd().execute(user);
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

                        //if (dbHandler.isFriendsNull(loginResult.getAccessToken().getUserId())) {
                            try {
                                JSONArray friends = response.getJSONObject().getJSONArray("data");
                                String s = "";
                                for (int i = 0; i < friends.length(); i++) {
                                    JSONObject rec = friends.getJSONObject(i);
                                    s += rec.getString("id") + "-";
                                }
                                Friendship f = new Friendship();
                                f.setMe(loginResult.getAccessToken().getUserId());
                                f.setMyFriends(s);

                                new FriendshipAdd().execute(f);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("elif", response.getJSONObject().toString());
                        //}
                    }
                }
                ).executeAsync();







                /*GraphRequest r = new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.d("kaan", response.toString());
                            }
                        }
                );
                Bundle b = new Bundle();
                b.putString("fields",
                        "id,name,email,gender, birthday, likes");
                r.setParameters(b);
                r.executeAsync();*/

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
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this,
                requestCode, resultCode, data);

        if (cbm.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    //ekledim Elif


    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    public void login_linkedin(){
        LISessionManager.getInstance(getApplicationContext()).init(this,
                buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {

                        Toast.makeText(getApplicationContext(), "success" , Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onAuthError(LIAuthError error) {

                        Toast.makeText(getApplicationContext(), "failed " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }, true);
    }

    public void continueToInfo(View view){

        /*for(int i =0; i<likenames.size(); i++)
        {
            Log.d("kaan", likenames.get(i) + " " + i);
        }*/



        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }


    private class UserAdd extends
            AsyncTask<User, Void, Void> {

        protected Void doInBackground(User... u) {
            DBHandler.insertUser(u[0]);
            return null;
        }
    }

    private class FriendshipAdd extends
            AsyncTask<Friendship, Void, Void> {

        protected Void doInBackground(Friendship... f) {
            DBHandler.insertFriendship(f[0]);
            return null;
        }
    }



}