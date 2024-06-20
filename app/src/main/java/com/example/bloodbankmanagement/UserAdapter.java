package com.example.bloodbankmanagement;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder>{


    ArrayList<User> users;
    Context context;
    MyOnClickListener myOnClickListenerCall,myOnClickListenerShare;


    public void updateList(ArrayList<User>users){
        this.users = users;
        notifyDataSetChanged();
    }


    public UserAdapter(Context context,ArrayList<User> users, MyOnClickListener onClickListenerCall,MyOnClickListener onClickListenerShare){
        this.context = context;
        this.users = users;
        this.myOnClickListenerCall = onClickListenerCall;
        this.myOnClickListenerShare = onClickListenerShare;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(context).inflate(R.layout.details_donor_requester,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        String state,district,town,fname,pincode,bloodgroup;
        state = users.get(position).getState();
        district = users.get(position).getDistrict();
        town = users.get(position).getTown();
        pincode = users.get(position).getPincode();
        bloodgroup = users.get(position).getBloodGroup();
        fname = String.format("%s %s", users.get(position).getFName(), users.get(position).getLName());
        String s = convertToTitleCase(fname);

        holder.state.setText("State: "+state);
        holder.district.setText("District: "+district);
        holder.town.setText("Town: "+town);
        holder.pincode.setText("Pincode: "+pincode);
        holder.fullName.setText(s);
        holder.bloodGroup.setText("Blood Group: "+bloodgroup);

        if(bloodgroup.equals("A+")){
            holder.bloodGroupImage.setImageResource(R.drawable.ap);
        }else if(bloodgroup.equals("A-")) {
            holder.bloodGroupImage.setImageResource(R.drawable.am);
        }else if(bloodgroup.equals("B+")) {
            holder.bloodGroupImage.setImageResource(R.drawable.bp);
        }else if(bloodgroup.equals("B-")) {
            holder.bloodGroupImage.setImageResource(R.drawable.bm);
        }
        else if(bloodgroup.equals("AB+")) {
            holder.bloodGroupImage.setImageResource(R.drawable.abp);
        }
        else if(bloodgroup.equals("AB-")) {
            holder.bloodGroupImage.setImageResource(R.drawable.abm);
        }
        else if(bloodgroup.equals("O+")) {
            holder.bloodGroupImage.setImageResource(R.drawable.op);
        }
        else if(bloodgroup.equals("O-")) {
            holder.bloodGroupImage.setImageResource(R.drawable.om);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.state.setTooltipText(state);
            holder.district.setTooltipText(district);
            holder.town.setTooltipText(town);
            holder.pincode.setTooltipText(pincode);
            holder.fullName.setTooltipText(fname);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        holder.share.setOnClickListener(v -> myOnClickListenerShare.getPosition(position));
        holder.call.setOnClickListener(v -> myOnClickListenerCall.getPosition(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserHolder extends RecyclerView.ViewHolder{

        TextView fullName,bloodGroup,state,district,town,pincode;

        ImageView share,call;

        ImageView bloodGroupImage;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.detailFullName);
            bloodGroup = itemView.findViewById(R.id.detailBloodGroup);
            state = itemView.findViewById(R.id.detailState);
            district = itemView.findViewById(R.id.detailDistrict);
            town = itemView.findViewById(R.id.detailTown);
            pincode = itemView.findViewById(R.id.detailPincode);
            call = itemView.findViewById(R.id.call);
            share = itemView.findViewById(R.id.share);
            bloodGroupImage = itemView.findViewById(R.id.bloodImg);

        }
    }

    public String convertToTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String str : arr) {
            sb.append(Character.toUpperCase(str.charAt(0)))
                    .append(str.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}

