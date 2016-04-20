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
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
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
    private static ArrayList<String> deletedFriends;
    private List<String> panpiks = new ArrayList<>();
    private RecyclerView recyclerView;
    private SimpleStringRecyclerViewAdapter mAdapter;
    ProgressDialog dialog;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.friend_list_recycler_view);

        mAdapter = new SimpleStringRecyclerViewAdapter(this.getApplicationContext(), panpiks);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                String u = panpiks.get(position);
                String[] parts = u.split("-");
                String part1 = parts[0]; // 004
                String part2 = parts[1]; // 034556
                Toast.makeText(getApplicationContext(), part1 + " is selected!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), FriendProfileActivity.class);
                i.putExtra("id", part2);
                startActivity(i);
            }
        }));

        dialog = ProgressDialog.show(this, "Finding...","Your friends are hiding frown emoticon", false);


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

    public void deleteButtonClicked(View v)
    {
        Toast.makeText( v.getContext(), "User has been deleted successfully.", Toast.LENGTH_SHORT).show();

        mAdapter.notifyDataSetChanged();
    }


    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> users;
        public ArrayList<String> selectedUsers;


        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView userNameTextView;
            public final SwipeLayout swipeLayout;

            ImageButton deleteFriendButton;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                userNameTextView = (TextView) view.findViewById(R.id.friendname);
                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe_friend);
                deleteFriendButton = (ImageButton) itemView.findViewById(R.id.deleteFriendButton);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + userNameTextView.getText();
            }
        }


        public String getValueAt(int position) {
            return users.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<String> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            users = items;
            selectedUsers = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_row, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {


            holder.mBoundString = users.get(position);
            String[] parts = holder.mBoundString.split("-");
            String part1 = parts[0]; // 004
            String part2 = parts[1]; // 034556
            holder.userNameTextView.setText(part1);
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

            // Drag From Right
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wrapper));


            holder.deleteFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedUsers.add(holder.mBoundString);
                    users.remove(holder.mBoundString);
                    Log.d("kaan", holder.mBoundString);
                    notifyDataSetChanged();
                    Toast.makeText(v.getContext(), "Clicked on" + holder.userNameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                    setDeletedFriends(selectedUsers);
                }
            });

            String userId = part2;
            Glide.with(holder.mImageView.getContext())
                    .load( getProfilePicture( userId ) )
                    .fitCenter()
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        String getProfilePicture( String userId ) {
            return "http://graph.facebook.com/" + userId + "/picture?type=square";
        }
    }

    public static ArrayList<String> getDeletedFriends()
    {
        return deletedFriends;
    }

    public static void setDeletedFriends(ArrayList<String> del)
    {
        deletedFriends = del;
    }

    @Override
    public void onBackPressed() {
        Log.d("elif", "sa");
        ArrayList<String> deleted = getDeletedFriends();
        DBHandler.deleteFriends(deleted);
        Log.d("elif", "as1");
        Intent i = new Intent(this, ProfileFragment.class);
        startActivity(i);
        Log.d("elif", "as2");
    }


}