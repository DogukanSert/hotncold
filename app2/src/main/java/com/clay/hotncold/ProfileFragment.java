
package com.clay.hotncold;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileFragment extends AppCompatActivity {

    ImageView profileImage;

    ArrayList<String> likes;
    ArrayList<String> likenames;
    ArrayList<String> favoriteMovies;
    ArrayList<String> favoriteMusic;
    TextView like, music, movies;
    String l,m, mo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        l="";
        m="";
        mo="";
        likes =new ArrayList<>();
        likenames =new ArrayList<>();

        favoriteMovies = new ArrayList<>();
        favoriteMusic = new ArrayList<>();

        like = (TextView) findViewById(R.id.like);
        music = (TextView) findViewById(R.id.music);
        movies = (TextView)findViewById(R.id.movies);
        //profile = Profile.getCurrentProfile();

        /*if (profile != null) {
            //getting username
            userName = (TextView) findViewById(R.id.information);
            userName.setText(profile.getName());
        }*/

        String id = AccessToken.getCurrentAccessToken().getUserId();

        GraphRequest g =new GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/likes",
            null,
            HttpMethod.GET,
            new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {

                    JSONObject obj = response.getJSONObject();
                    Log.d("kaan", "kasan");
                    JSONArray array;
                    try {
                        array = obj.getJSONArray("data");

                        for(int i = 0 ; i < array.length() ; i++){
                            likes.add(i, array.getJSONObject(i).getString("id"));
                            //Log.d("kaan", likes.get(i) + " " + i);
                        }
                    } catch (JSONException e) {
                        Log.d("kaan", e.toString());
                    }

                    for(int i=0; i<likes.size(); i++) {
                        new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/"+likes.get(i),
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    try {
                                        //Log.d("kaan" , response.getJSONObject().getString("name"));
                                        if(!likenames.contains(response.getJSONObject().getString("name"))) {
                                            likenames.add(response.getJSONObject().getString("name"));
                                            l += response.getJSONObject().getString("name") + "\n";
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    like.setText(l);
                                }
                            }
                        ).executeAsync();
                    }
                }
            }
        );

        Bundle p1 = new Bundle();
        p1.putString("fields",
                "likes");
        g.setParameters(p1);
        g.executeAsync();

        new GraphRequest(AccessToken.getCurrentAccessToken(),
            "/me/music",
            null,
            HttpMethod.GET,
            new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                    try {
                        JSONArray movie = response.getJSONObject().getJSONArray("data");
                        for (int i = 0; i < movie.length(); i++) {
                            JSONObject node = movie.getJSONObject(i);
                            String movieName = node.getString("name");
                            Log.d("kaan", movieName);
                            favoriteMusic.add(movieName);
                            m += movieName + "\n";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    music.setText(m);
                }
            }
        ).executeAsync();

        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "/me/movies",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray movie = response.getJSONObject().getJSONArray("data");
                            for (int i = 0; i < movie.length(); i++) {
                                JSONObject node = movie.getJSONObject(i);
                                String movieName = node.getString("name");
                                Log.d("kaan", movieName);
                                favoriteMovies.add(movieName);
                                mo += movieName + "\n";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        movies.setText(mo);
                    }
                }
        ).executeAsync();


        //getting profile picture
        profileImage = (ImageView)findViewById(R.id.backdrop);
            //profileImage.setProfileId(profile.getId());


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(id);

        loadBackdrop();
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        //PUT FACEBOOK IMAGE THERE
        //Glide.with(this).load(profileImage).centerCrop().into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    public void getFriendListView(View view){
        Intent i = new Intent(this, FriendListActivity.class);
        startActivity(i);
    }
}