package com.clay.hotncold;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

public class FriendListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //private List<User> users = new ArrayList<>();
    private static ArrayList<String> deletedFriends;
    private List<String> panpiks = new ArrayList<>();
    public ArrayList<String> newPanpiks ;
    private RecyclerView recyclerView;
    private SimpleStringRecyclerViewAdapter mAdapter;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("HotnCold");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.friend_list_recycler_view);
        newPanpiks = new ArrayList<>();
        dialog = ProgressDialog.show(this, "Finding...","Your friends are hiding frown emoticon", false);


        new Thread(new Runnable() {
            @Override
            public void run()
            {
                prepareData();

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflater.inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        //return super.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void prepareData()
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

                        newPanpiks.clear();
                        for(String s: panpiks)
                            newPanpiks.add(s);

                        if(mAdapter != null)
                            mAdapter.notifyDataSetChanged();

                        mAdapter = new SimpleStringRecyclerViewAdapter(getApplicationContext(), newPanpiks);


                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);

                    }
                }
        ).executeAsync();
    }

    //For Search Method

    @Override
    public boolean onQueryTextChange(String query) {
        Log.d("filter", "panpiks size: " + panpiks.size()+ "");
        Log.d("filter", "query: " + query);

        final List<String> filteredFriendList = filter(newPanpiks, query);
        mAdapter.animateTo(filteredFriendList);
        recyclerView.scrollToPosition(0);
        //mAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<String> filter(List<String> models, String query) {
        Log.d("filter", "models size: " +models.size() + "");
        query = query.toLowerCase();
        final List<String> filteredModelList = new ArrayList<>();
        for (String model : models) {
            final String text = model.toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
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
            ImageButton goFriendProfileButton;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                userNameTextView = (TextView) view.findViewById(R.id.friendname);
                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe_friend);
                deleteFriendButton = (ImageButton) itemView.findViewById(R.id.deleteFriendButton);
                goFriendProfileButton = (ImageButton)itemView.findViewById(R.id.goFriendProfileButton);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + userNameTextView.getText();
            }
        }

        public String removeItem(int position) {
            final String model = users.remove(position);
            notifyItemRemoved(position);
            return model;
        }

        public void addItem(int position, String model) {
            users.add(position, model);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition) {
            final String model = users.remove(fromPosition);
            users.add(toPosition, model);
            notifyItemMoved(fromPosition, toPosition);
        }

        //Search algorithms

        public void animateTo(List<String> models) {
            applyAndAnimateRemovals(models);
            applyAndAnimateAdditions(models);
            applyAndAnimateMovedItems(models);
        }

        private void applyAndAnimateRemovals(List<String> newModels) {
            for (int i = users.size() - 1; i >= 0; i--) {
                final String model = users.get(i);
                if (!newModels.contains(model)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<String> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final String model = newModels.get(i);
                if (!users.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<String> newModels) {
            for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
                final String model = newModels.get(toPosition);
                final int fromPosition = users.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }

        public String getValueAt(int position) {
            return users.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<String> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            for(String  s : items)
                Log.d("filter", "item: " + s);
            users  = new ArrayList<>(items);
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

            holder.goFriendProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent i = new Intent(context, FriendProfileActivity.class);
                    holder.mBoundString = users.get(position);
                    String[] parts = holder.mBoundString.split("-");
                    String part1 = parts[0]; // 004
                    String part2 = parts[1]; // 034556
                    i.putExtra("id", part2);
                    context.startActivity(i);
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
        ArrayList<String> deleted = getDeletedFriends();
        DBHandler.deleteFriends(deleted);
        Intent i = new Intent(this, ProfileFragment.class);
        startActivity(i);
    }


}