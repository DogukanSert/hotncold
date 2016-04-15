package com.clay.hotncold;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import java.util.List;


public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<String> users;

    public Adapter(List<String> friendlistOfUser) {
        this.users = friendlistOfUser;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String currentFriend = users.get(position);
        String[] parts = currentFriend.split("-");
        String part1 = parts[0]; // 004
        String part2 = parts[1]; // 034556
        holder.friendnametext.setText(part1);
        holder.friendicon.setProfileId(part2);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView friendnametext;
        public ProfilePictureView friendicon;

        public MyViewHolder(View view) {
            super(view);
            friendnametext = (TextView) view.findViewById(R.id.friendnametext);
            friendicon = (ProfilePictureView) view.findViewById(R.id.friendicon);
        }
    }

    /*public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView friendnameTitle;
        ProfilePictureView friendphoto;

        public MyViewHolder(View itemView) {
            super(itemView);

            friendnameTitle = (TextView) itemView.findViewById(R.id.friendnametext);
            friendphoto = (ProfilePictureView) itemView.findViewById(R.id.friendicon);
        }
    }*/
}