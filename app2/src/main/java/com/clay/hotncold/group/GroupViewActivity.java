package com.clay.hotncold.group;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clay.hotncold.R;

import java.util.ArrayList;

public class GroupViewActivity extends AppCompatActivity {

    ArrayList<String> groupMembers;
    TextView memberList;
    String groupname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        memberList = (TextView) findViewById(R.id.groupMembers);

        groupMembers = new ArrayList<>();

        groupname = getIntent().getStringExtra("groupname");

        Group g = new Group(groupname);

        memberList.setText(groupname);

        //Log.d("as", g.getMygroupFriends().toString());

        for(int i = 0; i < g.getMygroupFriends().size(); i++)
        {
            memberList.setText(g.getMygroupFriends().get(i));
        }


        loadBackdrop();

    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);

    }

}
