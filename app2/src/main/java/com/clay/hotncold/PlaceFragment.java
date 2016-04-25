package com.clay.hotncold;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PlaceFragment extends Fragment implements SearchView.OnQueryTextListener {

    private List<Place> myPlaces;
    private RecyclerView recyclerView;
    private PlaceFragmentAdapter mAdapter;

    public PlaceFragment() {
        // Required empty public constructor
    }

    public static PlaceFragment newInstance(String param1, String param2) {
        PlaceFragment fragment = new PlaceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place, container, false);
        //Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);
        //Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //getActivity().setSupportActionBar(toolbar);
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //setHasOptionsMenu(true);
        myPlaces = new ArrayList<>();
        recyclerView = (RecyclerView)view.findViewById(R.id.place_list_recycler_view);
        mAdapter = new PlaceFragmentAdapter(getActivity(),getData());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private List<Place> getData() {
        List<Place> places = new ArrayList<>();
        int[] icons = {R.drawable.ic_airport,
                R.drawable.ic_park,
                R.drawable.ic_art,
                R.drawable.ic_bar,
                R.drawable.ic_bus,
                R.drawable.ic_car,
                R.drawable.ic_museum,
                R.drawable.ic_post_office,
                R.drawable.ic_restaurant,
                R.drawable.ic_subway,
                R.drawable.ic_taxi,
                R.drawable.ic_university,
                R.drawable.ic_zoo,
        };

        String[] placeNames = {"Airport", "Amusement Park","Art Gallery", "Bar","Bus Station",
                "Car Rental","Museum","Post Office","Restaurant","Subway Station","Taxi","University"
                ,"Zoo"};

        List<Place> p = new ArrayList<>();
        for(int i = 0; i < placeNames.length && i < icons.length; i++)
        {
            Place current = new Place();
            current.setPlace_name(placeNames[i]);
            current.setIconId(icons[i]);
            places.add(current);
            p.add(current);
        }
        myPlaces.clear();
        for(Place s: p)
            myPlaces.add(s);

        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();

        mAdapter = new PlaceFragmentAdapter(getActivity(), myPlaces);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        return places;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<Place> filteredPlace = filter(myPlaces, query);
        mAdapter.animateTo(filteredPlace);
        recyclerView.scrollToPosition(0);
        return true;
    }
    private List<Place> filter(List<Place> models, String query) {
        Log.d("filter", "models size: " + models.size() + "");
        query = query.toLowerCase();
        final List<Place> filteredModelList = new ArrayList<>();
        for (Place model : models) {
            final String text = model.getPlace_name().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public static class PlaceFragmentAdapter
            extends RecyclerSwipeAdapter<PlaceFragmentAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Place> places = Collections.emptyList();


        @Override
        public int getSwipeLayoutResourceId(int position) {
            return 0;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView userNameTextView;
            public final SwipeLayout swipeLayout;
            //int iconId;

            ImageButton goToCameraButton;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.placeicon);
                userNameTextView = (TextView) view.findViewById(R.id.places);
                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe_places);
                goToCameraButton = (ImageButton) itemView.findViewById(R.id.cameraButton);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + userNameTextView.getText();
            }
        }

        public Place removeItem(int position) {
            final Place model = places.remove(position);
            notifyItemRemoved(position);
            return model;
        }

        public void addItem(int position, Place model) {
            places.add(position, model);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition) {
            final Place model = places.remove(fromPosition);
            places.add(toPosition, model);
            notifyItemMoved(fromPosition, toPosition);
        }

        //Search algorithms

        public void animateTo(List<Place> models) {
            applyAndAnimateRemovals(models);
            applyAndAnimateAdditions(models);
            applyAndAnimateMovedItems(models);
        }

        private void applyAndAnimateRemovals(List<Place> newModels) {
            for (int i = places.size() - 1; i >= 0; i--) {
                final Place model = places.get(i);
                if (!newModels.contains(model)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<Place> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final Place model = newModels.get(i);
                if (!places.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<Place> newModels) {
            for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
                final Place model = newModels.get(toPosition);
                final int fromPosition = places.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }


        public Place getValueAt(int position) {
            return places.get(position);
        }

        public PlaceFragmentAdapter(Context context, List<Place> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            places = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.place_row, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {


           Place currentPlace = places.get(position);
            holder.mBoundString = currentPlace.getPlace_name();
            holder.userNameTextView.setText(currentPlace.getPlace_name());
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            holder.mImageView.setImageResource(currentPlace.getIconId());

            // Drag From Right
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wrapper));


            holder.goToCameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //need to go to the camera view
                    Toast.makeText(v.getContext(), "Clicked on" + holder.userNameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                    //notifyDataSetChanged();

                }
            });

        }
        @Override
        public int getItemCount() {
            return places.size();
        }
    }
}

