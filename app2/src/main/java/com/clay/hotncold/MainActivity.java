package com.clay.hotncold;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.clay.hotncold.filter.FilterActivity;
import com.clay.hotncold.group.CreateGroupFragment;
import com.clay.hotncold.group.Group;
import com.clay.hotncold.group.GroupFragment;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements GroupFragment.GroupFragmentListener
{

    //Defining Variables
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;//EYE
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
    private AdvertiseCallback advertiseCallback;
    private static BluetoothLeAdvertiser adv;

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
        navigationView.setItemIconTintList(null);

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

        checkBluetoothConnection();

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
                        MapFragment fragment = new MapFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.replace(R.id.frame, fragment);
                        fragmentTransaction1.commit();
                        return true;


                    case R.id.mode:
                        BeaconFragment fr = new BeaconFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction3.replace(R.id.frame, fr);
                        fragmentTransaction3.commit();

                        return true;

                    case R.id.camera:
                        Intent in = new Intent(getApplicationContext(), CameraActivity.class);
                        in.putExtra("type", 2);
                        in.putExtra("id", AccessToken.getCurrentAccessToken().getUserId());
                        startActivity(in);

                        return true;

                    case R.id.filters:
                        Intent i = new Intent(getApplicationContext(), FilterActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.groups:
                        GroupFragment frag = new GroupFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction4.replace(R.id.frame, frag);
                        fragmentTransaction4.commit();
                        return true;
                    case R.id.settings:

                        return true;
                    case R.id.places:
                        Toast.makeText(getApplicationContext(),"Log out Selected",Toast.LENGTH_SHORT).show();
                        PlaceFragment pf = new PlaceFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction6 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction6.replace(R.id.frame, pf);
                        fragmentTransaction6.commit();
                        return true;
                    case R.id.logout:
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                checkBluetoothConnection();
            } else {
                finish();
            }
        }*/
    }
    private void checkBluetoothConnection() {
        BluetoothManager manager = (BluetoothManager) getApplicationContext().getSystemService(
                Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = manager.getAdapter();
        if (btAdapter == null) {
            showFinishingAlertDialog("Bluetooth Error", "Bluetooth not detected on device");
        } else if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else if (!btAdapter.isMultipleAdvertisementSupported()) {
            showFinishingAlertDialog("Not supported", "BLE advertising not supported on this device");
        } else {
            adv = btAdapter.getBluetoothLeAdvertiser();
            advertiseCallback = createAdvertiseCallback();
            new CreateBeacon(adv, advertiseCallback);
        }
    }

    private void showFinishingAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }

    private AdvertiseCallback createAdvertiseCallback() {
        return new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {
                switch (errorCode) {
                    case ADVERTISE_FAILED_DATA_TOO_LARGE:
                        showToastAndLogError("ADVERTISE_FAILED_DATA_TOO_LARGE");
                        break;
                    case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                        showToastAndLogError("ADVERTISE_FAILED_TOO_MANY_ADVERTISERS");
                        break;
                    case ADVERTISE_FAILED_ALREADY_STARTED:
                        showToastAndLogError("ADVERTISE_FAILED_ALREADY_STARTED");
                        break;
                    case ADVERTISE_FAILED_INTERNAL_ERROR:
                        showToastAndLogError("ADVERTISE_FAILED_INTERNAL_ERROR");
                        break;
                    case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                        showToastAndLogError("ADVERTISE_FAILED_FEATURE_UNSUPPORTED");
                        break;
                    default:
                        showToastAndLogError("startAdvertising failed with unknown error " + errorCode);
                        break;
                }
            }
        };
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showToastAndLogError(String message) {
        showToast(message);
        Log.e("HEY", message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    @Override
    public void onBackPressed() {
        MapFragment fragment = new MapFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.replace(R.id.frame, fragment);
        fragmentTransaction1.commit();
    }

    public String getProfilePicture(String id)
    {
        return "http://graph.facebook.com/"+id+"/picture?type=large&redirect=true&width=600&height=600";
    }
    @Override
    public void fabButtonClicked(View v) {
        CreateGroupFragment cb = new CreateGroupFragment();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, cb);
        transaction.commit();
    }

    public void goToCameraPlaces(View v) {
        String types = PlaceFragment.getPlaces();
        types = removeLastChar(types);
        Intent i = new Intent(getApplicationContext(),PlacesCameraActivity.class);
        i.putExtra("types", types);
        startActivity(i);

    }

    // TODO: DB işi var yine amk
    public void doneButtonClicked(View v) {
        ArrayList<String> friends = CreateGroupFragment.getSelectedfriend();

        String friendsDB = "";

        if(friends == null){
            Toast.makeText(getApplicationContext(),"Group must contain a friend.",Toast.LENGTH_SHORT).show();
            return;
        }

        for(int i=0; i<friends.size(); i++) {
            String[] parts = friends.get(i).split("-");
            friendsDB += parts[1] + "-";
            Log.d("kaan", friends.get(i));
        }

        if(CreateGroupFragment.getGroupName().length()<6){
            Toast.makeText(getApplicationContext(),"Group name must be at least 6 characters.",Toast.LENGTH_SHORT).show();
            return;
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

    private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }

    public void goToCameraFromBeacon(View v)
    {
        Intent i = new Intent(this, CameraActivity.class);
        i.putExtra("type", 3);
        i.putExtra("id", BeaconFragment.getBeaconIds());
        startActivity(i);
    }

}
