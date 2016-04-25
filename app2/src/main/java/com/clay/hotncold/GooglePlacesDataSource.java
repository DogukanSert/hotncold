package com.clay.hotncold;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.jwetherell.augmented_reality.R;
import com.jwetherell.augmented_reality.data.NetworkDataSource;
import com.jwetherell.augmented_reality.ui.IconMarker;
import com.jwetherell.augmented_reality.ui.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class extends DataSource to fetch data from Google Places.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class GooglePlacesDataSource extends NetworkDataSource {

	private static final String URL = "https://maps.googleapis.com/maps/api/place/search/json?";
	private static  String types ;

	private static String key = null;
	private static Bitmap icon = null;

	public GooglePlacesDataSource(Resources res) {
		if (res == null) throw new NullPointerException();

		key = res.getString(R.string.google_places_api_key);

		createIcon(res);
	}

	protected void createIcon(Resources res) {
		if (res == null) throw new NullPointerException();

		icon = BitmapFactory.decodeResource(res, R.drawable.buzz);
	}

	@Override
	public String createRequestURL(double lat, double lon, double alt, float radius, String locale) {
		try {
			Log.d("places", "url: " + URL + "location="+lat+","+lon+"&radius="+(radius*1000.0f)+"&types="+PlacesCameraActivity.getTypes()+"&sensor=true&key="+key);
			return URL + "location="+lat+","+lon+"&radius="+(radius*1000.0f)+"&types="+PlacesCameraActivity.getTypes()+"&sensor=true&key="+key;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public List<Marker> parse(String URL) {
		if (URL == null) throw new NullPointerException();

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

		return parse(json);
	}

	@Override
	public List<Marker> parse(JSONObject root) {
		if (root == null) throw new NullPointerException();

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
		return markers;
	}

	private Marker processJSONObject(JSONObject jo) {
		if (jo == null) throw new NullPointerException();

		if (!jo.has("geometry")) throw new NullPointerException();

		Marker ma = null;
		try {
			Double lat = null, lon = null;

			if (!jo.isNull("geometry")) {
				JSONObject geo = jo.getJSONObject("geometry");
				JSONObject coordinates = geo.getJSONObject("location");
				lat = Double.parseDouble(coordinates.getString("lat"));
				lon = Double.parseDouble(coordinates.getString("lng"));
			}
			if (lat != null) {
				String user = jo.getString("name");

				ma = new IconMarker(user + ": " + jo.getString("name"), lat, lon, 0, Color.RED, icon, "0");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ma;
	}
}