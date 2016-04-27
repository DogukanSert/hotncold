package com.clay.hotncold;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


public class FriendRequestFragment extends Fragment {

    private List<String> myRequests;
    private RecyclerView recyclerView;
    private RequestRecyclerViewAdapter mAdapter;
    private List<String> panpiks = new ArrayList<>();
    DBHandler db;

    public FriendRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.friend_request_recycler_view);

        mAdapter = new RequestRecyclerViewAdapter(getActivity(),panpiks);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this.getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                String u = panpiks.get(position);
                String[] parts = u.split("-");
                String part1 = parts[0]; // 004
                String part2 = parts[1]; // 034556
                //String s = myRequests.get(position);
                //g.setGroupName(myGroups.get(position).getGroupName());
                Intent i = new Intent(getActivity(), FriendProfileActivity.class);
                i.putExtra("id",part2 );
                startActivity(i);
            }
        }));*/


        new Thread(new Runnable() {
            @Override
            public void run()
            {
                prepareData(AccessToken.getCurrentAccessToken().getUserId());

                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {

                        dialog.dismiss();
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

  public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

      private GestureDetector gestureDetector;
      private ClickListener clickListener;



      @Override
      public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
          return false;
      }

      @Override
      public void onTouchEvent(RecyclerView rv, MotionEvent e) {

      }

      @Override
      public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

      }
  }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public static class RequestRecyclerViewAdapter
            extends RecyclerSwipeAdapter<RequestRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> friendRequests;
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

            ImageButton acceptRequestButton;
            ImageButton refuseRequestButton;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                userNameTextView = (TextView) view.findViewById(R.id.requestName);
                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe_request);
                acceptRequestButton = (ImageButton) itemView.findViewById(R.id.acceptRequest);
                refuseRequestButton = (ImageButton) itemView.findViewById(R.id.refuseRequest);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + userNameTextView.getText();
            }
        }


        public String getValueAt(int position) {
            return friendRequests.get(position);
        }

        public RequestRecyclerViewAdapter(Context context, List<String> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            friendRequests = items;
            selectedUsers = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.request_row, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {


            holder.mBoundString = friendRequests.get(position);
            String[] parts = holder.mBoundString.split("-");
            String part1 = parts[0]; // 004
            String part2 = parts[1]; // 034556
            holder.userNameTextView.setText(part1);
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

            // Drag From Right
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wrapper));


            holder.acceptRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add to the database, the new friend of the user
                    selectedUsers.add(holder.mBoundString);
                    friendRequests.remove(holder.mBoundString);
                    notifyDataSetChanged();
                    Toast.makeText(v.getContext(), "Clicked on" + holder.userNameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

            holder.refuseRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //only delete the request
                    friendRequests.remove(holder.mBoundString);
                    notifyDataSetChanged();
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
            return friendRequests.size();
        }

        String getProfilePicture( String userId ) {
            return "http://graph.facebook.com/" + userId + "/picture?type=large&redirect=true&width=600&height=600";
        }
    }


}
