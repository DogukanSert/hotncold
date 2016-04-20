package com.clay.hotncold;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clay.hotncold.group.CreateGroupFragment;
import com.clay.hotncold.group.Group;
import com.clay.hotncold.group.GroupFragment;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements GroupFragment.GroupFragmentListener
{

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    TextView username;
    TextView email;
    //FriendListFragment friendlistFragment;
    ProfilePictureView profilePic;
    //Profile p;
    private View headerView;
    String name;
    String mail;
    CircleImageView s;
    ImageView profileimage;

    RelativeLayout headerLayout;

    public void onheaderClick(View v) {
        drawerLayout.closeDrawers();
        Intent intent = new Intent(this, ProfileFragment.class);

        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        headerLayout =(RelativeLayout) navigationView.getHeaderView(0);
        profileimage = (ImageView) headerLayout.findViewById(R.id.profile_image);
        Glide.with(this).load(getProfilePicture(AccessToken.getCurrentAccessToken().getUserId())).
                centerCrop().into(profileimage);

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        name = object.optString("name");
                        mail = object.optString("email");


                        username = (TextView) headerLayout.findViewById(R.id.username);
                        email = (TextView) headerLayout.findViewById(R.id.email);

                        //s.setImageURI(Uri.parse());

                        username.setText(name);
                        email.setText(mail);

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields",
                "name,email");
        request.setParameters(parameters);
        request.executeAsync();

        Toast.makeText(getApplicationContext(),"Profile",Toast.LENGTH_SHORT).show();
        MapFragment fragment = new MapFragment();
        //FilterFragment fragment = new FilterFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){

                    case R.id.map:
                        Toast.makeText(getApplicationContext(),"Profile",Toast.LENGTH_SHORT).show();
                        MapFragment fragment = new MapFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.replace(R.id.frame, fragment);
                        fragmentTransaction1.commit();
                        return true;

                    case R.id.profile:
                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.mode:
                        Toast.makeText(getApplicationContext(),"Beacon Selected",Toast.LENGTH_SHORT).show();
                        BeaconFragment fr = new BeaconFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction3.replace(R.id.frame, fr);
                        fragmentTransaction3.commit();

                        return true;

                    case R.id.camera:
                        Intent in = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(in);

                        return true;

                    case R.id.filters:
                        Toast.makeText(getApplicationContext(),"Filters Selected",Toast.LENGTH_SHORT).show();
                        FilterFragment fra = new FilterFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.replace(R.id.frame, fra);
                        fragmentTransaction2.commit();
                        return true;
                    case R.id.groups:
                        Toast.makeText(getApplicationContext(),"Groups Selected",Toast.LENGTH_SHORT).show();
                        GroupFragment frag = new GroupFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction4.replace(R.id.frame, frag);
                        fragmentTransaction4.commit();
                        return true;
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(),"Settings Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.logout:
                        Toast.makeText(getApplicationContext(),"Log out Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void aboutButtonClicked(View v)
    {

        aboutFragment = new AboutFragment();
        FragmentManager fm = getFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.hide(profileFragment);
        transaction.replace(R.id.frame, aboutFragment);
        transaction.commit();
    } */

    /*public void friendsButtonClicked(View v)
    {

        friendlistFragment = new FriendListFragment();
        FragmentManager fm = getFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.hide(profileFragment);
        transaction.replace(R.id.frame, friendlistFragment);
        transaction.commit();
    }*/

    @Override
    public void onBackPressed() {
        MapFragment fragment = new MapFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.replace(R.id.frame, fragment);
        fragmentTransaction1.commit();
    }

    public Bitmap loadBitmap(String url)
    {
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try
        {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

    public String getProfilePicture(String id)
    {
        return "http://graph.facebook.com/"+id+"/picture?type=square";
    }
    @Override
    public void fabButtonClicked(View v) {
        CreateGroupFragment cb = new CreateGroupFragment();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, cb);
        transaction.commit();
    }



    // TODO: DB işi var yine amk
    public void doneButtonClicked(View v) {
        ArrayList<String> friends = CreateGroupFragment.getSelectedfriend();

        String friendsDB = "";

        for(int i=0; i<friends.size(); i++) {
            String[] parts = friends.get(i).split("-");
            friendsDB += parts[1] + "-";
            Log.d("kaan", friends.get(i));
        }

        String groupName = CreateGroupFragment.getGroupName() + "-" + AccessToken.getCurrentAccessToken().getUserId();

        Group g = new Group();
        g.setGroupName(groupName);
        g.setMyFriends(friendsDB);

        DBHandler.groupInsert(g);


        Toast.makeText( v.getContext(), "Group has been created successfully.", Toast.LENGTH_SHORT).show();
        GroupFragment cb = new GroupFragment();
        FragmentManager fm = getFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, cb);
        transaction.commit();
    }


}
