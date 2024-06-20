package com.example.bloodbankmanagement.ui.display_requests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.Toast;

import com.example.bloodbankmanagement.R;
import com.example.bloodbankmanagement.User;
import com.example.bloodbankmanagement.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.bloodbankmanagement.databinding.FragmentDisplayRequestsBinding;

public class DisplayRequestsFragment extends Fragment {
    RecyclerView list;
    ArrayList<User>requests,temp;
    UserAdapter adapter;
    SearchView districtFilter;
    User self;
    String uid = FirebaseAuth.getInstance().getUid();
    Button requestCancelBtn;

    private FragmentDisplayRequestsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDisplayRequestsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeComponents();

        getRequests();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initializeComponents() {
        requestCancelBtn = binding.btnAddRequest;
        requestCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the user clicks on the Request Blood button, then the user is added to the list of requests
                updateBloodRequest(!self.getRequestBlood().equals("True"));
            }
        });

        temp = new ArrayList<>();
        requests = new ArrayList<>();

        list = binding.getRoot().findViewById(R.id.requestList);
        adapter = new UserAdapter(requireContext(), requests, position -> {
            //call button handle
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+temp.get(position).getMobile()));
            startActivity(intent);
        }, position -> {
            //share button handle
            User sent = temp.get(position);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "*Urgent*\n"+ sent.getFName()+" requires "+sent.getBloodGroup()+" blood urgently.\nPlease contact "+sent.getFName()+" at "+sent.getMobile()+" for further details.\n\nHis/Her Contact details are:\nName: *"+sent.getFName()+" "+sent.getLName()+"*\nPhone Number: *"+sent.getMobile()+"*\nCity/Town/Village: *"+sent.getTown()+"*\nPinCode: *"+sent.getPincode()+"*\nDistrict: *"+sent.getDistrict()+"*\nState: *"+sent.getState()+"*\n\n*Please share this message with your friends and family.*");
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });


        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(adapter);

        districtFilter = binding.getRoot().findViewById(R.id.districtFilterRequest);

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

    // Retrieving the list of requests from the database
    private void getRequests() {
        // Retrieving the list of requests from the database
        FirebaseDatabase.getInstance("https://blood-bank-management-306c0-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Donors").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requests.clear();
                temp.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    User user = ds.getValue(User.class);

                    // If the user is a donor and has requested for blood, then the user is added to the list of requests
                    assert user != null;
                    if(user.getStep().equals("Done")) {
                        if (user.getRequestBlood().equals("True")) {
                            requests.add(user);
                            temp.add(user);
                        }
                        if (user.getUID().equals(uid)) {
                            self = user;
                            if (self.getRequestBlood().equals("True")) {
                                requestCancelBtn.setText("Cancel Blood Request");
                            } else {
                                requestCancelBtn.setText("Request Blood");
                            }
                        }
                    }
                }
                filterList(districtFilter.getQuery().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void filterList(String text){
        temp.clear();
        for( User request : requests){
            if(request.getDistrict().toLowerCase().contains(text.toLowerCase())) {
                temp.add(request);
            }
        }

        if (temp.isEmpty()){
            Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
        }
        adapter.updateList(temp);
    }

    // This method updates the request blood status of the user
    private void updateBloodRequest(boolean b) {
        HashMap<String,Object> hashMap = new HashMap<>();
        if(b){
            hashMap.put("RequestBlood","True");
        }else {
            hashMap.put("RequestBlood","False");
        }
        FirebaseDatabase.getInstance("https://blood-bank-management-306c0-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Donors").child(uid).updateChildren(hashMap);
    }
}