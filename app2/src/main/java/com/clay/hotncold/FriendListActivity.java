package com.clay.hotncold;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    //private List<User> users = new ArrayList<>();
    private List<String> panpiks = new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter mAdapter;
    ProgressDialog dialog;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new Adapter(panpiks);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String u = panpiks.get(position);
                String[] parts = u.split("-");
                String part1 = parts[0]; // 004
                String part2 = parts[1]; // 034556
                Toast.makeText(getApplicationContext(), part1 + " is selected!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), FriendProfileActivity.class);
                i.putExtra("id", part2);
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        dialog = ProgressDialog.show(this, "Finding...","Your friends are hiding :(", false);

        /*final ProgressDialog dialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Finding...");
        dialog.setMessage("Your friends are hiding :(");
        dialog.show();*/
        //dbHandler = new DBHandler();

        //new GetFriends().execute(AccessToken.getCurrentAccessToken().getUserId());

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                prepareData(AccessToken.getCurrentAccessToken().getUserId());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {

                        dialog.dismiss();
                    }
                });
            }
        }).start();

    }

    public void prepareData(String id)
    {
        /*ArrayList<User> u = dbHandler.getFriends(id);
        for(int i=0; i<u.size(); i++)
            users.add(u.get(i));*/

        /*new GraphRequest(loginResult.getAccessToken(), "/me/friends", null, HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {

                if (dbHandler.isFriendsNull(loginResult.getAccessToken().getUserId())) {
                    try {
                        JSONArray friends = response.getJSONObject().getJSONArray("data");

                        for (int i = 0; i < friends.length(); i++) {
                            JSONObject rec = friends.getJSONObject(i);
                            dbHandler.addFriendToDB(loginResult.getAccessToken().getUserId(), rec.getString("id"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("elif", response.getJSONObject().toString());
                }
            }
        }
        ).executeAsync();*/

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONArray friends = null;
                        try {
                            friends = response.getJSONObject().getJSONArray("data");

                            for (int i = 0; i < friends.length(); i++) {
                                JSONObject rec = friends.getJSONObject(i);
                                panpiks.add(rec.getString("name") + "-" + rec.getString("id"));
                                Log.d("kaan", panpiks.get(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                }
        ).executeAsync();

        /*new GraphRequest(AccessToken.getCurrentAccessToken(), "/1187807821254099",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("kaan", response.getJSONObject().optString("name"));
                    }
                }
        ).executeAsync();*/


    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private FriendListActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FriendListActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }


            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    /*private class GetFriends extends AsyncTask<String, Integer, ArrayList<User>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // DIALOG_DOWNLOAD_PROGRESS is defined as 0 at start of class
            //showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        protected ArrayList<User> doInBackground(String... me) {
            //ArrayList<User> users = new ArrayList<>();

            ArrayList<User> u = dbHandler.getFriends(me[0]);
            for(int i=0; i<u.size(); i++)
                users.add(u.get(i));
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            super.onPostExecute(users);
            mAdapter.notifyDataSetChanged();
        }
    }*/

}
