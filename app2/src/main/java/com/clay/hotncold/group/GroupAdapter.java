package com.clay.hotncold.group;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.clay.hotncold.R;

import java.util.Collections;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> implements Filterable {

    private List<Group> groups = Collections.emptyList();

    public GroupAdapter(List<Group> groupsOfUser) {

        this.groups = groupsOfUser;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Group currentGroup = groups.get(position);
        holder.groupNametext.setText(currentGroup.getGroupName());
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView groupNametext;
        public ImageView groupicon;

        public MyViewHolder(View view) {
            super(view);
            groupNametext = (TextView) view.findViewById(R.id.groupnametext);
            groupicon = (ImageView) view.findViewById(R.id.groupicon);
        }
    }
}
