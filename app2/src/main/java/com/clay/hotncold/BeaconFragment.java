package com.clay.hotncold;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BeaconFragment extends Fragment {

    private static final String TAG = "EYE";
    private static final long SCAN_TIME_MILLIS = 2000;

    public ArrayList<Beacon> arrayList;
    private ScanCallback scanCallback;
    private BluetoothLeScanner scanner;
    private RecyclerView recyclerView;
    private SimpleStringRecyclerViewAdapter mAdapter;
    private AdvertiseCallback advertiseCallback;
    private BluetoothLeAdvertiser adv;
    public static String friendid;


    // Receives the runnable that stops scanning after SCAN_TIME_MILLIS.
    private static final Handler handler = new Handler(Looper.getMainLooper());

    // An aggressive scan for nearby devices that reports immediately.
    private static final ScanSettings SCAN_SETTINGS =
            new ScanSettings.Builder().
                    setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setReportDelay(0)
                    .build();

    // The Eddystone-UID frame type byte.
    // See https://github.com/google/eddystone for more information.
    private static final byte EDDYSTONE_UID_FRAME_TYPE = 0x00;

    // The Eddystone Service UUID, 0xFEAA.
    private static final ParcelUuid EDDYSTONE_SERVICE_UUID =
            ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB");

    // A filter that scans only for devices with the Eddystone Service UUID.
    private static final ScanFilter EDDYSTONE_SCAN_FILTER = new ScanFilter.Builder()
            .setServiceUuid(EDDYSTONE_SERVICE_UUID)
            .build();

    private static final List<ScanFilter> SCAN_FILTERS = buildScanFilters();

    private static List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();
        scanFilters.add(EDDYSTONE_SCAN_FILTER);
        return scanFilters;
    }

    private static final Comparator<Beacon> RSSI_COMPARATOR = new Comparator<Beacon>() {
        @Override
        public int compare(Beacon lhs, Beacon rhs) {
            return ((Integer) rhs.rssi).compareTo(lhs.rssi);
        }
    };



    void scanBeacon() {
        scanCallback = new ScanCallback();
        scanner.startScan(SCAN_FILTERS, SCAN_SETTINGS, scanCallback);
        Log.i(TAG, "starting scan");
        try {
            Thread.sleep(SCAN_TIME_MILLIS);
        }catch (InterruptedException e) {
            Log.e(TAG, "error in sleep");
            return;
        }
        Runnable stopScanning = new Runnable() {
            @Override
            public void run() {
                scanner.stopScan(scanCallback);
                Log.i(TAG, "stopped scan");
            }
        };

        handler.postDelayed(stopScanning, SCAN_TIME_MILLIS);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        arrayList = new ArrayList<>();

        checkBluetoothConnection();
        new CreateBeacon(adv, advertiseCallback);

        createScanner();
        scanBeacon();
    }

    protected static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    private boolean arrayListContainsId(ArrayList<Beacon> list, byte[] id) {
        for (Beacon beacon : list) {
            if (Arrays.equals(beacon.id, id)) {
                return true;
            }
        }
        return false;
    }

    private void createScanner() {
        BluetoothManager btManager =
                (BluetoothManager)getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constants.REQUEST_CODE_ENABLE_BLE);
        }
        else if (!btAdapter.isMultipleAdvertisementSupported()) {
            showFinishingAlertDialog("Not supported", "BLE advertising not supported on this device");
        }
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Log.e(TAG, "Can't enable Bluetooth");
            Toast.makeText(getActivity(), "Can't enable Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        scanner = btAdapter.getBluetoothLeScanner();
    }

    // Pops an AlertDialog that quits the app on OK.
    private void showFinishingAlertDialog(String title, String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {getActivity().finish();
                    }
                }).show();
    }


    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private void showToastAndLogError(String message) {
        showToast(message);
        Log.e(TAG, message);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(
                R.layout.fragment_beacon, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        setRecyclerView(recyclerView);
        return v;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = (ImageView) view.findViewById(R.id.backdrop);
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openBeaconMap(v);
            }
        });
        Glide.with(this).load(R.drawable.stars_fixed).centerCrop().into(imageView);
    }

    private void openBeaconMap( View view ){
        //// open beacon map as an activity here or fragment but I think activity is better.
        MapFragment fragment = new MapFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

            mAdapter = new SimpleStringRecyclerViewAdapter(getActivity(),
                    getBeaconNames());
            recyclerView.setAdapter(mAdapter);
           // mAdapter.notifyDataSetChanged();

            Log.v(TAG, "array list is empty on create " + mAdapter.beaconsNames.toString());

    }

    ArrayList<String> getBeaconNames() {
        ArrayList<String> beaconNames = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        for(int i = 0; i < arrayList.size(); i++ ) {
            double distance = getDistance( arrayList.get(i).rssi, arrayList.get(i).txPower );
            beaconNames.add(arrayList.get(i).getHexId() + "\n" + distance + "m" );

            Log.v(TAG, "distance" + distance);
            //beaconNames.add(arrayList.get(i).getHexId() + "\n" + arrayList.get(i).rssi + "m" + "\n" + date + " ( " + hour + ":" + min + " ) ");
        }
        return beaconNames;
    }

    double getDistance(int rssi, int txPower) {
    /*
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)
     *
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */

        return Math.pow(10, ((double) txPower - rssi) / (10 * 3))/100;
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerSwipeAdapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> beaconsNames;

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return 0;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView userNameTextView;
            public final SwipeLayout swipeLayout;TextView tvDelete;
            TextView tvEdit;
            TextView tvShare;
            ImageButton btnLocation;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                /**
                 *
                 */
                userNameTextView = (TextView) view.findViewById(R.id.username);

                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
                tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
                tvEdit = (TextView) itemView.findViewById(R.id.tvEdit);
                tvShare = (TextView) itemView.findViewById(R.id.tvShare);
                btnLocation = (ImageButton) itemView.findViewById(R.id.btnLocation);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + userNameTextView.getText();
            }
        }


        public String getValueAt(int position) {
            return beaconsNames.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<String> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            beaconsNames = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.beacon_list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if(holder == null ) {
                Log.v(TAG, "holderr is null");
            }
            friendid = BeaconFragment.hexToString(beaconsNames.get(position));

            User us;
            BeaconFragment.GetUser x = new BeaconFragment.GetUser();
            x.execute();
            us = null;
            try {
                us = x.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            holder.mBoundString = beaconsNames.get(position);
            holder.userNameTextView.setText(us.getUsername() + " " + us.getSurname());
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

            // Drag From Left
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.swipeLayout.findViewById(R.id.bottom_wrapper1));

            // Drag From Right
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wrapper));

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ProfileFragment.class);
                    intent.putExtra("beacon_id", holder.mBoundString);

                    context.startActivity(intent);
                    return true;
                }
            });

            holder.btnLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(v.getContext(), "Clicked on Map " + holder.userNameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });


            holder.tvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(view.getContext(), "Clicked on Share " + holder.userNameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

            holder.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(view.getContext(), "Clicked on Edit  " + holder.userNameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });


            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemManger.removeShownLayouts(holder.swipeLayout);
                    beaconsNames.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, beaconsNames.size());
                    mItemManger.closeAllItems();
                    Toast.makeText(view.getContext(), "Deleted " + holder.userNameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

            /**
             *
             */
            Glide.with(holder.mImageView.getContext())
                    .load( getProfilePicture( us.getFacebookID() ) )
                    .fitCenter()
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            Log.v(TAG, "item count  " + beaconsNames.size());
            return beaconsNames.size();
        }

        String getProfilePicture( String userId ) {
            return "http://graph.facebook.com/" + userId + "/picture?type=square";
        }
    }

    public class ScanCallback extends android.bluetooth.le.ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            ScanRecord scanRecord = result.getScanRecord();
            if (scanRecord == null) {
                Log.w(TAG, "Null ScanRecord for device " + result.getDevice().getAddress());
                return;
            }

            byte[] serviceData = scanRecord.getServiceData(EDDYSTONE_SERVICE_UUID);
            if (serviceData == null) {
                return;
            }

            // We're only interested in the UID frame time since we need the beacon ID to register.
            if (serviceData[0] != EDDYSTONE_UID_FRAME_TYPE) {
                return;
            }

            // Extract the beacon ID from the service data. Offset 0 is the frame type, 1 is the
            // Tx power, and the next 16 are the ID.
            // See https://github.com/google/eddystone/eddystone-uid for more information.
            byte[] id = Arrays.copyOfRange(serviceData, 2, 18);
            if (arrayListContainsId(arrayList, id)) {
                return;
            }
            byte[] txPower = Arrays.copyOfRange(serviceData, 0, 2); //!!!! 1 ya da 2 emin degilim
            // Draw it immediately and kick off a async request to fetch the registration status,
            // redrawing when the server returns.
            Log.i(TAG, "id " + Utils.toHexString(id) + ", rssi " + result.getRssi());

            Byte txP = new Byte( txPower[0] );
            double accuracy = calculateAccuracy( txP.intValue(), result.getRssi());
            Log.i(TAG, "id " + Utils.toHexString(id) + ", accuracy " + accuracy);
            Beacon beacon = new Beacon("EDDYSTONE", id, Beacon.STATUS_ACTIVE, result.getRssi(), txP);

            arrayList.add(beacon);
            setRecyclerView(recyclerView);
            Log.v(TAG, "Beacon Name" + arrayList.get(0).getHexId());
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "onScanFailed errorCode " + errorCode);
        }
    }

    private void checkBluetoothConnection() {
        BluetoothManager btManager =
                (BluetoothManager)getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constants.REQUEST_CODE_ENABLE_BLE);
        }
        else if (!btAdapter.isMultipleAdvertisementSupported()) {
            Toast.makeText(getContext(), "Not supported BLE advertising not supported on this device", Toast.LENGTH_SHORT).show();
        }
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Log.e(TAG, "Can't enable Bluetooth");
            Toast.makeText(getContext(), "Can't enable Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        //scanner = btAdapter.getBluetoothLeScanner();

        adv = btAdapter.getBluetoothLeAdvertiser();
        advertiseCallback = createAdvertiseCallback();
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

    public static String hexToString(String s)
    {
        String hexStr = s;
        BigInteger bigInt = new BigInteger(hexStr, 16);
        return bigInt.toString();
    }

    public static class GetUser extends
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
