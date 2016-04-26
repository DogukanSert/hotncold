package com.clay.hotncold.filter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.clay.hotncold.FriendProfileActivity;
import com.clay.hotncold.R;
import java.util.ArrayList;
import java.util.List;

import com.clay.hotncold.DBHandler;
import com.clay.hotncold.R;
import com.clay.hotncold.User;

import java.util.ArrayList;

/**
 * Created by dogukan on 26.03.16.
 */
public class FilterFragmentGender extends Fragment implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerview;
    private List<FilterObject> mFilterObject;
    private FilterAdapter adapter;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_filter_name, container, false);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview_filter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setLayoutManager(layoutManager);

        setHasOptionsMenu(true);

        db = new DBHandler();
        ArrayList<User> userObjects;
        userObjects = DBHandler.getAllUsers();

        //  String[] locales = Locale.getISOCountries();
        mFilterObject = new ArrayList<>();
        String birthday = "";

        for(User u : userObjects){
            if(u.getBirthday() != null){
                birthday = u.getBirthday();
            }
            mFilterObject.add(new FilterObject(u.getUsername() + " " + u.getSurname(),u.getGender(),
                    birthday, u.getFacebookID()));
        }

        adapter = new FilterAdapter(mFilterObject,new OnItemClickListener() {
            @Override public void onItemClick(FilterObject item) {
                Intent i = new Intent(getContext(), FriendProfileActivity.class);
                i.putExtra("id", item.getFbID());
                startActivity(i);
            }
        });
        recyclerview.setAdapter(adapter);

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_filter, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        adapter.setFilter(mFilterObject);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<FilterObject> filteredModelList = filter(mFilterObject, query);
        adapter.setFilter(filteredModelList);
        return true;
    }

    private List<FilterObject> filter(List<FilterObject> models, String query) {
        query = query.toLowerCase();
        if(query.startsWith("m")){
            query = " " + query;
        }

        final List<FilterObject> filteredModelList = new ArrayList<>();
        for (FilterObject model : models) {
            final String text = model.getGender().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

}