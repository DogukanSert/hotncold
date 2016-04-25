package com.clay.hotncold.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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

import com.bumptech.glide.Glide;
import com.clay.hotncold.CameraActivity;
import com.clay.hotncold.DBHandler;
import com.clay.hotncold.FriendProfileActivity;
import com.clay.hotncold.R;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

public class GroupViewActivity extends AppCompatActivity {

    ArrayList<String> groupMembers = new ArrayList<>();
    //TextView memberList;
    String groupname;
    private RecyclerView recyclerView;
    private StringRecyclerViewAdapter mAdapter;
    boolean isClicked;


    public boolean isClicked() {
        return isClicked;
    }

    public void setIsClicked(boolean isClicked) {
        this.isClicked = isClicked;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        isClicked = false;
        groupname = getIntent().getStringExtra("groupname");
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.colbar);
        collapsingToolbar.setTitle(groupname);

        Group g = DBHandler.getGroup(groupname);
        groupMembers = g.getMygroupFriends();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        for(int i = 0; i < g.getMygroupFriends().size(); i++)
        {
            Log.d("group", g.getMygroupFriends().get(i));
        }



        recyclerView = (RecyclerView) findViewById(R.id.group_friendlist_recycler_view);
        mAdapter = new StringRecyclerViewAdapter(this.getApplicationContext(), groupMembers);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String s = groupMembers.get(position);
                Intent i = new Intent(getApplicationContext(), FriendProfileActivity.class);
                i.putExtra("id", s);
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/



        loadBackdrop();



    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);

    }

    public void toCamera(View v)
    {
        Intent in = new Intent(getApplicationContext(), CameraActivity.class);
        in.putExtra("type", 1);
        in.putExtra("id", groupname);
        startActivity(in);
    }


    public static class StringRecyclerViewAdapter
            extends RecyclerView.Adapter<StringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> users;
        public ArrayList<String> selectedUsers;
        public boolean isClicked;


        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView userNameTextView;
            public final SwipeLayout swipeLayout;
            public boolean isClicked = true;

            ImageButton deleteFriendButton;
            ImageButton goToProfileButton;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                userNameTextView = (TextView) view.findViewById(R.id.groupfriendname);
                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe_groupView);
                deleteFriendButton = (ImageButton) itemView.findViewById(R.id.deleteFriendButton);
                goToProfileButton = (ImageButton) itemView.findViewById(R.id.goToProfile);
            }


        }


        public String getValueAt(int position) {
            return users.get(position);
        }

        public StringRecyclerViewAdapter(Context context, List<String> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            users = items;
            selectedUsers = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.groupview_row, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {


            holder.mBoundString = users.get(position);
            String id = users.get(position);
            holder.userNameTextView.setText(DBHandler.getUser(holder.mBoundString).getUsername() + " " + DBHandler.getUser(holder.mBoundString).getSurname());
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

            // Drag From Right
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wrapper));

            /*holder.mView.setOnClickListener(new View.OnClickListener(){


                @Override
                public void onClick(View v) {
                       Log.d("click", Boolean.toString(isClicked));
                       Context context = v.getContext();
                       String s = users.get(position);
                       Intent i = new Intent(context, FriendProfileActivity.class);
                       i.putExtra("id", s);
                       context.startActivity(i);
                }
            });*/


            holder.deleteFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedUsers.add(holder.mBoundString);
                    users.remove(holder.mBoundString);
                    Log.d("kaan", holder.mBoundString);
                    notifyDataSetChanged();
                    //Toast.makeText(v.getContext(), "Clicked on" + holder.userNameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

            holder.goToProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = v.getContext();
                    Intent i = new Intent(context, FriendProfileActivity.class);
                    i.putExtra("id", holder.mBoundString);
                    context.startActivity(i);
                    //Toast.makeText(v.getContext(), "Clicked on" + holder.userNameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

            String userId = users.get(position);
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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
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

}
