package com.example.bloodbankmanagement.ui.display_donors;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;

import com.example.bloodbankmanagement.User;
import com.example.bloodbankmanagement.UserAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.bloodbankmanagement.databinding.FragmentDisplayDonorsBinding;

public class DisplayDonorsFragment extends Fragment {

    RecyclerView list;
    UserAdapter adapter;
    ArrayList<User> users,temp;
    SearchView districtFilter;
    User self;
    String uid = FirebaseAuth.getInstance().getUid();
    private FragmentDisplayDonorsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDisplayDonorsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initializing all the variables
        initializeComponents();

        // Setting the adapter for the list of donors
        getDonors();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void initializeComponents() {
        self = new User();
        districtFilter = binding.districtFilter;
        list = binding.donorsList;
        users = new ArrayList<>();
        temp = new ArrayList<>();
        adapter = new UserAdapter(requireContext(), users, position -> {
            // Handling call button event
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+temp.get(position).getMobile()));
            startActivity(intent);
        }, position -> {
            // Handling share button event
            User sent = temp.get(position);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "*"+sent.getFName()+"* is ready to donate blood.\n\n His Contact Details are as follows:\n\n*Name:* "+sent.getFName()+" "+sent.getLName()+"\n*Blood Group:* "+sent.getBloodGroup()+"\n*Address:* "+sent.getState()+" "+sent.getDistrict()+" "+sent.getTown()+"\n*Mobile Number:* "+sent.getMobile()+"\n\n*Please reach out to him if you need blood.*");
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);

        });

        // Setting the layout manager for the list of donors
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(adapter);
        districtFilter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }

    // This method gets the list of donors from the database
    private void getDonors() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://blood-bank-management-306c0-default-rtdb.asia-southeast1.firebasedatabase.app/");

        database.getReference("Donors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                temp.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    User user = ds.getValue(User.class);

                    // Adding the donor to the list only if the donor is verified and visible
                    assert user != null;
                    if(user.getStep().equals("Done")) {
                        if (user.getVisible().equals("True")) {
                            users.add(user);
                            temp.add(user);
                        }
                    }
                }

                // Updating the list of donors
                filterList(districtFilter.getQuery().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterList(String text){
        temp.clear();
        for( User user : users){
            if(user.getDistrict().toLowerCase().contains(text.toLowerCase())) {
                temp.add(user);
            }
        }

        if (temp.isEmpty()){
            Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
        }

        adapter.updateList(temp);
    }
}