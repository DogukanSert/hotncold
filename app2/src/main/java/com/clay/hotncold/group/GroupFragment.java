package com.clay.hotncold.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clay.hotncold.DBHandler;
import com.clay.hotncold.R;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends Fragment implements SearchView.OnQueryTextListener {

    private List<Group> myGroups;
    private static ArrayList<Group> deletedGroups;
    EditText inputGroupSearch;
    private RecyclerView recyclerView;
    private GroupFragmentAdapter mAdapter;
    DBHandler db;
    //private OnFragmentInteractionListener mListener;

    public GroupFragment() {
        // Required empty public constructor
    }

    GroupFragmentListener listener;


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<Group> filteredGroup = filter(myGroups, query);
        mAdapter.animateTo(filteredGroup);
        recyclerView.scrollToPosition(0);
        return true;
    }

    private List<Group> filter(List<Group> models, String query) {
        Log.d("filter", "models size: " + models.size() + "");
        query = query.toLowerCase();
        final List<Group> filteredModelList = new ArrayList<>();
        for (Group model : models) {
            final String text = model.getGroupName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    //interface
    public interface GroupFragmentListener {
        void fabButtonClicked(View v);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, container, false);
        //inputGroupSearch = (EditText) view.findViewById(R.id.inputGroupSearch);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        myGroups = DBHandler.getMyGroups();

        for(Group g : myGroups)
        {
            String name = g.getGroupName();
            String[] parts = name.split("-");
            g.setGroupName(parts[0]);
            String myFriends = g.getMyFriends();
            g.setMyFriends(myFriends);
        }

        mAdapter = new GroupFragmentAdapter(getActivity(),myGroups);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        //when user selects the fab in order to add new group

        fab.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        fabClicked(v);
                    }
                }
        );

        return view;
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (GroupFragmentListener) context ;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
    public void fabClicked(View v)
    {
        listener.fabButtonClicked(v);
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }


            });

        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }

    public void deleteGroupButtonClicked(View v)
    {
        Toast.makeText( v.getContext(), "Group has been deleted successfully.", Toast.LENGTH_SHORT).show();

        mAdapter.notifyDataSetChanged();
    }




    public static class GroupFragmentAdapter
            extends RecyclerSwipeAdapter<GroupFragmentAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Group> groups;
        public ArrayList<Group> selectedGroups;

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

            ImageButton deleteGroupButton;
            ImageButton goToGroupButton;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.groupicon);
                userNameTextView = (TextView) view.findViewById(R.id.groupnametext);
                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe_groups);
                deleteGroupButton = (ImageButton) itemView.findViewById(R.id.deleteGroupButton);
                goToGroupButton = (ImageButton)itemView.findViewById(R.id.goToGroupButton);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + userNameTextView.getText();
            }
        }

        public Group removeItem(int position) {
            final Group model = groups.remove(position);
            notifyItemRemoved(position);
            return model;
        }

        public void addItem(int position, Group model) {
            groups.add(position, model);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition) {
            final Group model = groups.remove(fromPosition);
            groups.add(toPosition, model);
            notifyItemMoved(fromPosition, toPosition);
        }

        //Search algorithms

        public void animateTo(List<Group> models) {
            applyAndAnimateRemovals(models);
            applyAndAnimateAdditions(models);
            applyAndAnimateMovedItems(models);
        }

        private void applyAndAnimateRemovals(List<Group> newModels) {
            for (int i = groups.size() - 1; i >= 0; i--) {
                final Group model = groups.get(i);
                if (!newModels.contains(model)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<Group> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final Group model = newModels.get(i);
                if (!groups.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<Group> newModels) {
            for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
                final Group model = newModels.get(toPosition);
                final int fromPosition = groups.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }


        public Group getValueAt(int position) {
            return groups.get(position);
        }

        public GroupFragmentAdapter(Context context, List<Group> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            groups = items;
            selectedGroups = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_row, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {


            final Group currentGroup = groups.get(position);
            holder.mBoundString = currentGroup.getGroupName();
            holder.userNameTextView.setText(currentGroup.getGroupName());
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

            // Drag From Right
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wrapper));


            holder.deleteGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //selectedGroups.add(currentGroup);
                    groups.remove(currentGroup);
                    Toast.makeText(v.getContext(), "Clicked on" + holder.userNameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    setDeletedGroups(selectedGroups);

                }
            });

            holder.goToGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent i = new Intent(context, GroupViewActivity.class);
                    i.putExtra("groupname", holder.mBoundString);
                    Toast.makeText(v.getContext(), "Clicked on" + holder.userNameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                    context.startActivity(i);
                }
            });


        }

        @Override
        public int getItemCount() {
            return groups.size();
        }


    }

    public static ArrayList<Group> getDeletedGroups()
    {
        return deletedGroups;
    }

    public static void setDeletedGroups(ArrayList<Group> del)
    {
        deletedGroups = del;
    }
}




