package com.clay.hotncold;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.util.Log;

import com.facebook.AccessToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by ali deniz on 19.03.2016.
 */
public class CreateBeacon
{
    private static final String TAG = "EddystoneAdvertiser";
    private static final byte FRAME_TYPE_UID = 0x00;
    static String id;
    static String hexValue;
    User us;

    // The Eddystone Service UUID, 0xFEAA. See https://github.com/google/eddystone
    private static final ParcelUuid SERVICE_UUID =
            ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB");

    // Used to remember the most recently used UI settings.
    private SharedPreferences sharedPreferences;
    private BluetoothLeAdvertiser adv;
    private AdvertiseCallback advertiseCallback;
    private int txPowerLevel;
    private int advertiseMode;
    private String namespace;

    public CreateBeacon( BluetoothLeAdvertiser adv, AdvertiseCallback advertiseCallback) {
        this.adv = adv;
        this.advertiseCallback = advertiseCallback;
        id = AccessToken.getCurrentAccessToken().getUserId();
        CreateBeacon.GetUser x = new CreateBeacon.GetUser();
        x.execute();
        us = null;
        try {
            us = x.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d("kaan", e.toString());
        }
        startAdvertising();
    }


    private AdvertiseCallback createAdvertiseCallback() {
        return new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {

            }
        };
    }
    private void startAdvertising() {
        Log.i(TAG, "Starting ADV");

        AdvertiseSettings advertiseSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(advertiseMode)
                .setTxPowerLevel(txPowerLevel)
                .setConnectable(true)
                .build();

        byte[] serviceData = null;
        try {
            serviceData = buildServiceData();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .addServiceData(SERVICE_UUID, serviceData)
                .addServiceUuid(SERVICE_UUID)
                .setIncludeTxPowerLevel(false)
                .setIncludeDeviceName(false)
                .build();

        adv.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);

    }

    private void stopAdvertising() {
        Log.i(TAG, "Stopping ADV");
        adv.stopAdvertising(advertiseCallback);
    }


    private byte[] buildServiceData() throws IOException {
        byte txPower = (byte) -16;
        hexValue = us.getBeaconID();
        String namespace = hexValue.substring(0,10);
        Log.i(TAG, namespace);
        String instance = hexValue.substring(10);
        Log.i(TAG, instance);
        byte[] namespaceBytes = toByteArray(namespace);
        byte[] instanceBytes = toByteArray(instance);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(new byte[]{FRAME_TYPE_UID, txPower});
        os.write(namespaceBytes);
        os.write(instanceBytes);
        return os.toByteArray();
    }

    /*private boolean isValidHex(String s, int len) {
        return !(s == null || s.isEmpty()) && (s.length() / 2) == len && s.matches("[0-9A-F]+");
    }*/

    private byte[] toByteArray(String hexString) {
        // hexString guaranteed valid.
        int len = hexString.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return bytes;
    }

   /* private String randomHexString(int length) {
        byte[] buf = new byte[length];
        new Random().nextBytes(buf);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(String.format("%02X", buf[i]));
        }
        return stringBuilder.toString();
    }*/


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
            return DBHandler.getUser(id);
        }

        @Override
        protected void onPostExecute(User aVoid) {
            super.onPostExecute(aVoid);
            //dialog.dismiss();
        }
    }

}