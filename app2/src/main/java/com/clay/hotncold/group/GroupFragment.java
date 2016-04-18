package com.clay.hotncold.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.clay.hotncold.DBHandler;
import com.clay.hotncold.R;

import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends Fragment {

    private List<Group> myGroups;
    EditText inputGroupSearch;
    private RecyclerView recyclerView;
    private GroupAdapter mAdapter;
    DBHandler db;
    //private OnFragmentInteractionListener mListener;

    public GroupFragment() {
        // Required empty public constructor
    }

    GroupFragmentListener listener;
    //interface
    public interface GroupFragmentListener {
        void fabButtonClicked(View v);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, container, false);
        inputGroupSearch = (EditText) view.findViewById(R.id.inputGroupSearch);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        //mAdapter = new GroupAdapter(getData());

        /*String[] groupnames = {"CS 101", "Close Friends", "Family", "High School", "College", "CS 492", "Digital Design", "Automata Class"};
        for(int i = 0; i < groupnames.length; i++){
            Group current = new Group(groupnames[i]);
            myGroups.add(current);
            current.addFriendstoGroup("Elif Ağım");
            current.addFriendstoGroup("Cemil Kaan Akyol");
            current.addFriendstoGroup("Doğukan Sert");
            current.addFriendstoGroup("Elif Yağmur Eğrice");
            current.addFriendstoGroup("Bahadır Ünal");
        }*/

        myGroups = DBHandler.getMyGroups();

        for(Group g : myGroups)
        {
            String name = g.getGroupName();
            String[] parts = name.split("-");
            g.setGroupName(parts[0]);
            String myFriends = g.getMyFriends();
            g.setMyFriends(myFriends);
        }

        mAdapter = new GroupAdapter(myGroups);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this.getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d("inside", "inside on click");
                Group g = myGroups.get(position);
                Log.d("inside", "position:" + position);
                g.setGroupName(myGroups.get(position).getGroupName());
                Log.d("groupname", g.getGroupName());
                Intent i = new Intent(getActivity(), GroupViewActivity.class);
                i.putExtra("groupname", g.getGroupName());
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        /**
         * Enabling Search Filter
         * */
        inputGroupSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                GroupFragment.this.mAdapter.getFilter().filter(cs);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        //when user selects the fab in order to add new group

        fab.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        fabClicked(v);
                    }
                }
        );


        return view;
    }

    public static List<Group> getData(){

        List<Group> mygroups = new ArrayList<>();
        String[] groupnames = {"CS 101", "Close Friends", "Family", "High School", "College", "CS 492", "Digital Design", "Automata Class"};
        for(int i = 0; i < groupnames.length; i++){
            Group current = new Group(groupnames[i]);
            mygroups.add(current);
        }
        return mygroups;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (GroupFragmentListener) context ;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
    public void fabClicked(View v)
    {
        listener.fabButtonClicked(v);
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




