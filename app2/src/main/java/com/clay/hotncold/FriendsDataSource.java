package com.clay.hotncold;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.facebook.AccessToken;
import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.data.NetworkDataSource;
import com.jwetherell.augmented_reality.ui.IconMarker;
import com.jwetherell.augmented_reality.ui.Marker;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class extends DataSource to fetch data from Google Places.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class FriendsDataSource extends NetworkDataSource {

	private static Bitmap icon = null;

	ArrayList<String> friends;
	ArrayList<UserLoc> locs;
    ArrayList<User> users;
	String id;
    public static final int MAX = 5;
    public static int count;
    String parts[];

	public FriendsDataSource(Resources res) {
		if (res == null) throw new NullPointerException();

        count=0;

		id = AccessToken.getCurrentAccessToken().getUserId();
		friends = DBHandler.getFriendIds(id);

        locs = new ArrayList<>();
        users = new ArrayList<>();

		//createIcon(res);
	}

	/*protected void createIcon(Resources res) {
		if (res == null) throw new NullPointerException();

		icon = BitmapFactory.decodeResource(res, R.drawable.buzz);
	}*/

	@Override
	public String createRequestURL(double lat, double lon, double alt, float radius, String locale) {
		/*try {
			return URL + "location="+lat+","+lon+"&radius="+(radius*1000.0f)+"&types="+TYPES+"&sensor=true&key="+key;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}*/
        return null;
	}

    /**
     *
     * Type 0 -> single user with id
     * Type 1 -> group members with groupname id
     * Type 2 -> all friends of user with id
     *
     * */



    @Override
    public List<Marker> getMarkerList(int type, String id, float radius)
    {
        Log.d("camerakaan", "Type: " + type + " id: " + id);
        if(count>MAX || count==0){
            locs.clear();
            users.clear();
            if(type==2){
                Log.d("camerakaan", type +"");
                for(String s : friends) {
                    locs.add(DBHandler.getFriendLoc(s));
                    users.add(DBHandler.getUser(s));
                }
            }
            else if(type == 1){
                String s = DBHandler.getGroup(id).getMyFriends();
                parts = s.split("-");
                for(int i=0; i<parts.length; i++) {
                    locs.add(DBHandler.getFriendLoc(parts[i]));
                    users.add(DBHandler.getUser(parts[i]));
                }
            }
            else if(type == 0){
                locs.add(DBHandler.getFriendLoc(id));
                users.add(DBHandler.getUser(id));
            }
            count=0;
        }
        List<Marker> markers = new ArrayList<>();
        Location last = ARData.getCurrentLocation();
        float myLat = (float) last.getLatitude();
        float myLon = (float) last.getLongitude();

        if(type == 0) {
            UserLoc us = locs.get(0);
            User u = users.get(0);
            if(distFrom(myLat, myLon, (float)us.getLat(), (float)us.getLon()) < radius*1000) {
                icon = getFacebookPPBitmap(getProfilePicture(us.getId()));
                markers.add(new IconMarker(u.getUsername() + " " + u.getSurname(),
                        us.getLat(), us.getLon(), us.getAlt(), Color.RED, icon, u.getFacebookID()));
                Log.d("camerakaan", us.getId());
            }
        }
        else if(type == 1) {
            for (int i = 0; i < parts.length; i++) {

                UserLoc us = locs.get(i);
                User u = users.get(i);
                if(distFrom(myLat, myLon, (float)us.getLat(), (float)us.getLon()) < radius*1000) {
                    icon = getFacebookPPBitmap(getProfilePicture(us.getId()));
                    markers.add(new IconMarker(u.getUsername() + " " + u.getSurname(),
                            us.getLat(), us.getLon(), us.getAlt(), Color.RED, icon, u.getFacebookID()));
                    Log.d("camerakaan", us.getId());
                }
            }
        }
        else if(type == 2) {

            for(int i =0; i<friends.size(); i++)
            {
                Log.d("camerakaan", i + "th getting friend" + radius);
                UserLoc us = locs.get(i);
                User u = users.get(i);
                if(distFrom(myLat, myLon, (float)us.getLat(), (float)us.getLon()) < radius*1000) {
                    icon = getFacebookPPBitmap(getProfilePicture(us.getId()));
                    markers.add(new IconMarker(u.getUsername() + " " + u.getSurname(),
                            us.getLat(), us.getLon(), us.getAlt(), Color.RED, icon, u.getFacebookID()));
                    Log.d("camerakaan", us.getId());
                }
            }

        }
        count++;

        Log.d("count", count + " count");
        return markers;
    }

	public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    private static Bitmap getFacebookPPBitmap(String url){
        URL facebookProfileURL= null;
        Bitmap bitmap=null;
        try {
            facebookProfileURL = new URL(url);
            bitmap = BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(bitmap==null)
            Log.d("camerakaan", "bitmap is null");

        return bitmap;
    }

    private String getProfilePicture(String id)
    {
        return "https://graph.facebook.com/"+id+"/picture?type=square";
    }

    @Override
    public List<Marker> parse(String URL) {
		/*if (URL == null) throw new NullPointerException();

		InputStream stream = null;
		stream = getHttpGETInputStream(URL);
		if (stream == null) throw new NullPointerException();

		String string = null;
		string = getHttpInputString(stream);
		if (string == null) throw new NullPointerException();

		JSONObject json = null;
		try {
			json = new JSONObject(string);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (json == null) throw new NullPointerException();

		return parse(json);*/
        return null;
    }

    @Override
    public List<Marker> parse(JSONObject root) {

		/*if (root == null) throw new NullPointerException();

		JSONObject jo = null;
		JSONArray dataArray = null;
		List<Marker> markers = new ArrayList<Marker>();

		try {
			if (root.has("results")) dataArray = root.getJSONArray("results");
			if (dataArray == null) return markers;
			int top = Math.min(MAX, dataArray.length());
			for (int i = 0; i < top; i++) {
				jo = dataArray.getJSONObject(i);
				Marker ma = processJSONObject(jo);
				if (ma != null) markers.add(ma);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return markers;*/
        return null;
    }
}