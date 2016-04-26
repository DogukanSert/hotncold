package com.clay.hotncold.filter;

/**
 * Created by dogukan on 25.04.16.
 */
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clay.hotncold.R;


public class ItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView mView_ImageView;
    public TextView name_TextView;
    public TextView gender_TextView;
    public TextView birthday_TextView;

    public ItemViewHolder(View itemView) {
        super(itemView);

        mView_ImageView = (ImageView) itemView.findViewById(R.id.avatar);
        name_TextView = (TextView) itemView.findViewById(R.id.name);
        gender_TextView = (TextView) itemView.findViewById(R.id.gender);
        birthday_TextView = (TextView) itemView.findViewById(R.id.birthday);
    }

    public void bind(final FilterObject filterObject,final OnItemClickListener listener) {
        name_TextView.setText(filterObject.getName());
        gender_TextView.setText(filterObject.getGender());
        birthday_TextView.setText(filterObject.getBirthday());

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(filterObject);
            }
        });

    }
}