package com.clay.hotncold;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by dogukan on 26.03.16.
 */
public class FilterFragment extends Fragment {
    private ListView lv;
    ArrayAdapter<String> adapter;
    EditText inputSearch;
    DBHandler db;
    String[] users;
    String[] userIds;
    ArrayList<User> userObjects;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        db = new DBHandler();

        userObjects = DBHandler.getAllUsers();

        users = new String[userObjects.size()];
        userIds = new String[userObjects.size()];
        int i = 0;
        for(User u : userObjects){
            users[i] = u.getUsername() + " " + u.getSurname();
            userIds[i] = u.getFacebookID();
            i++;
        }

        // Listview Data

        lv = (ListView) view.findViewById(R.id.list_view);
        inputSearch = (EditText) view.findViewById(R.id.inputSearch);

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this.getContext(), R.layout.list_item, R.id.product_name, users);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String a = parent.getAdapter().getItem(position).toString();
                Log.d("kaan", a + "");
                int pos=-1;
                for(int i = 0; i< userObjects.size(); i++)
                {
                    if(users[i].equals(a))
                        pos=i;
                }
                if(pos>-1) {

                    Intent intent = new Intent(getContext(), FriendProfileActivity.class);
                    intent.putExtra("id", userIds[pos]);
                    startActivity(intent);
                }

            }
        });

        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                FilterFragment.this.adapter.getFilter().filter(cs);
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
        return view;
    }


}