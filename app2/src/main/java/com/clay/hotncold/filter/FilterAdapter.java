package com.clay.hotncold.filter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.clay.hotncold.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dogukan on 25.04.16.
 */
public class FilterAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private List<FilterObject> mFilterObject;

    public FilterAdapter(List<FilterObject> mFilterObject) {
        this.mFilterObject = mFilterObject;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        final FilterObject model = mFilterObject.get(i);
        itemViewHolder.bind(model);
        Glide.with(itemViewHolder.mView_ImageView.getContext())
                .load( getProfilePicture( model.getFbID() ) )
                .fitCenter()
                .into(itemViewHolder.mView_ImageView);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mFilterObject.size();
    }

    public void setFilter(List<FilterObject> filterObjects) {
        mFilterObject = new ArrayList<>();
        mFilterObject.addAll(filterObjects);
        notifyDataSetChanged();
    }

    String getProfilePicture( String userId ) {
        return "http://graph.facebook.com/" + userId + "/picture?type=large&redirect=true&width=600&height=600";
    }
}
