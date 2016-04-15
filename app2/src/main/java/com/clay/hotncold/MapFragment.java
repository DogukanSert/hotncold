package com.clay.hotncold;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A fragment that launches other parts of the demo application.
 */
public class MapFragment extends Fragment implements LocationListener, SensorEventListener {

    MapView mapView;
    GoogleMap map;

    DBHandler dbHandler;

    /**
     location
     */
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1000 ;
    protected LocationManager locationManager;

    /**
     location
     */


    /**
     sensor
     */

    private SensorManager mSensorManager;
    private Sensor mSensor;
    float[] vals;
    static Marker[] markers;
    static boolean cont;

    static String id;
    static String friendid;

    /**
     * sensor
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        id = AccessToken.getCurrentAccessToken().getUserId();

        dbHandler = new DBHandler();
        startMap();

        return v;
    }

    public void startMap()
    {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        if (!isGPSEnabled && !isNetworkEnabled) {
            showSettingsAlert();
        }


        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                friendid = marker.getTitle();
                MapFragment.GetUser x = new MapFragment.GetUser();
                x.execute();
                User us = null;
                try {
                    us = x.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                assert us != null;
                Toast toast = Toast.makeText(getContext(), us.getUsername() + " " + us.getSurname()
                        + " clicked.", Toast.LENGTH_SHORT);
                toast.show();

                Intent intent = new Intent(getContext(), FriendProfileActivity.class);
                intent.putExtra("id", us.getFacebookID());
                startActivity(intent);

                return false;
            }
        });



        /***/

        /***/
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
        vals = new float[3];
        /***/

        latitude = 39.92;
        longitude = 32.86;

        UserLoc loc = new UserLoc();
        loc.setId(id);
        loc.setLat(latitude);
        loc.setLon(longitude);
        loc.setTime(0);
        loc.setSpeed(0);

        new LocationAdd().execute(loc);



        LatLng PERTH = new LatLng(latitude, longitude);
		/*Marker perth = map.addMarker(new MarkerOptions()
				.position(PERTH)
				.draggable(true));*/

        MapsInitializer.initialize(this.getActivity());

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(PERTH, 10);
        map.animateCamera(cameraUpdate);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                ArrayList<String> friends = dbHandler.getFriendIds(AccessToken.getCurrentAccessToken().getUserId());

                markers = new Marker[friends.size()];
                cont=true;

                for(int i=0; i<friends.size(); i++)
                {
                    UserLoc u = dbHandler.getFriendLoc(friends.get(i));
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putInt("first", 0);
                    b.putInt("i",i);
                    b.putString("id", friends.get(i));
                    b.putString("lat", u.getLat() + "");
                    b.putString("lon", u.getLon() + "");
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }

                while(cont)
                {
                    for(int i=0; i<friends.size(); i++)
                    {
                        UserLoc u = dbHandler.getFriendLoc(friends.get(i));
                        Message msgObj = handler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putInt("first", 1);
                        b.putInt("i",i);
                        b.putString("id", friends.get(i));
                        b.putString("lat", u.getLat() + "");
                        b.putString("lon", u.getLon() + "");
                        msgObj.setData(b);
                        handler.sendMessage(msgObj);
                    }
                }
            }

            private final android.os.Handler handler = new android.os.Handler() {

                @Override
                public void handleMessage(Message msg) {
                    Bundle b = msg.getData();
                    int first = b.getInt("first");
                    int i = b.getInt("i");
                    String lat = b.getString("lat");
                    String lon = b.getString("lon");
                    if(first==0) {
                        //if (Double.parseDouble(lat) != 0 || Double.parseDouble(lon) != 0) {
                        LatLng PERTH = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                        markers[i] = map.addMarker(new MarkerOptions()
                                .position(PERTH)
                                .draggable(true)
                                .title(b.getString("id")));

                        //}
                    }

                    else if (first==1)
                    {
                        markers[i].setPosition(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));
                    }

                }
            };
        };

        Thread t = new Thread(r);
        t.start();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void showSettingsAlert(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        alertDialog.setTitle("GPS is settings");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getContext().startActivity(intent);
                dialog.cancel();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }


    @Override
    public void onLocationChanged(Location location) {
        UserLoc u = new UserLoc();
        u.setId(id);
        u.setLat(location.getLatitude());
        u.setLon(location.getLongitude());
        u.setSpeed(location.getSpeed());
        u.setTime(location.getTime());
        new UpdateLoc().execute(u);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        UserLoc u = new UserLoc();
        u.setId(id);
        u.setLat(0);
        u.setLon(0);
        u.setSpeed(0);
        u.setTime(0);

        new UpdateLoc().execute(u);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        vals[0] = event.values[0];
        vals[1] = event.values[1];
        vals[2] = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class LocationAdd extends
            AsyncTask<UserLoc, Void, Void> {

        protected Void doInBackground(UserLoc... f) {
            DBHandler.insertLatLong(f[0]);
            return null;
        }
    }

    private class UpdateLoc extends
            AsyncTask<UserLoc, Void, Void> {

        protected Void doInBackground(UserLoc... f) {
            DBHandler.updateLatLong(f[0]);
            return null;
        }
    }

    private class GetUser extends
            AsyncTask<Void, Void, User> {
        //private ProgressDialog dialog;

        public GetUser() {
            //dialog = new ProgressDialog(FriendListActivity.class.getC);
        }

        protected void onPreExecute() {
            //this.dialog.setMessage("Progress start");
            //this.dialog.show();
        }

        protected User doInBackground(Void... f) {
            return DBHandler.getUser(friendid);
        }

        @Override
        protected void onPostExecute(User aVoid) {
            super.onPostExecute(aVoid);
            //dialog.dismiss();
        }
    }
}