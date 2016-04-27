package com.clay.hotncold.group;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clay.hotncold.DBHandler;
import com.clay.hotncold.R;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CreateGroupFragment extends Fragment {


    public static EditText inputGroupName;
    private List<String> panpiks = new ArrayList<>();
    private RecyclerView recyclerView;
    private SimpleStringRecyclerViewAdapter mAdapter;
    ProgressDialog dialog;
    DBHandler dbHandler;
    FloatingActionButton doneButton;

    public static ArrayList<String> selectedfriend;

    public CreateGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);
        inputGroupName = (EditText) view.findViewById(R.id.inputGroupName);

        recyclerView = (RecyclerView) view.findViewById(R.id.friendRecycleView);
        doneButton = (FloatingActionButton) view.findViewById(R.id.doneButton);

        mAdapter = new SimpleStringRecyclerViewAdapter(getActivity(),panpiks);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        new Thread(new Runnable() {
            @Override
            public void run()
            {
                prepareData(AccessToken.getCurrentAccessToken().getUserId());
/*
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        Log.d("elif","elif");
                    }
                });*/
            }
        }).start();



        return view;
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
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                }
        ).executeAsync();

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerSwipeAdapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> users;
        public ArrayList<String> selectedUsers;

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return 0;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView userNameTextView;
            public final SwipeLayout swipeLayout;

            ImageButton addFriendButton;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                userNameTextView = (TextView) view.findViewById(R.id.friendname);
                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
                addFriendButton = (ImageButton) itemView.findViewById(R.id.addFriendButton);
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
                    .inflate(R.layout.friendgroup, parent, false);
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


            holder.addFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedUsers.add(holder.mBoundString);
                    Log.d("elif", holder.mBoundString);
                    users.remove(holder.mBoundString);
                    Log.d("elif", users.toString());
                    notifyDataSetChanged();
                    Toast.makeText(v.getContext(), "Clicked on" + holder.userNameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                    CreateGroupFragment.setSelectedfriend(selectedUsers);
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
            return "http://graph.facebook.com/" + userId + "/picture?type=large&redirect=true&width=600&height=600";
        }
    }

    public static ArrayList<String> getSelectedfriend() {
        return selectedfriend;
    }

    public static void setSelectedfriend(ArrayList<String> sf) {
        selectedfriend = sf;
    }

    public static String getGroupName() {
        return inputGroupName.getText().toString();
    }


}


