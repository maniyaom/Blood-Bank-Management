package com.example.bloodbankmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodbankmanagement.databinding.ActivityNavigationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class NavigationActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigationBinding binding;
    String uid = FirebaseAuth.getInstance().getUid();
    User self;
    MenuItem visibleDonors;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigation.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_display_requests, R.id.nav_display_donors)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                NavController navController = Navigation.findNavController(NavigationActivity.this, R.id.nav_host_fragment_content_navigation);

                if (id == R.id.nav_display_requests) {
                    navController.navigate(R.id.nav_display_requests);
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_display_donors) {
                    navController.navigate(R.id.nav_display_donors);
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_change_password) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"Password Reset Link Sent On Registered Email.", Snackbar.LENGTH_LONG);
                    View view1 = snack.getView();
                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view1.getLayoutParams();
                    params.gravity = Gravity.CENTER_VERTICAL;
                    view1.setLayoutParams(params);

                    if(user.getEmail()!=null) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(user.getEmail());
                    } else {
                        snack.setText("Error Occurred!");
                    }
                    snack.show();
                    drawer.closeDrawers();
                    return false;

                } else if (id == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(),LoginScreenActivity.class));
                    NavigationActivity.this.finish();
                    drawer.closeDrawers();
                    return false;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);

        // Setting name and email of user in drawer and visible as donor checkbox
        View headerView = navigationView.getHeaderView(0);
        TextView nav_nameofuser = (TextView) headerView.findViewById(R.id.nav_nameofuser);
        TextView nav_emailofuser = (TextView) headerView.findViewById(R.id.nav_emailofuser);

        // Setting visible donors
        visibleDonors = menu.findItem(R.id.visibleDonors);

        updateNameEmailAndCheckBox(nav_nameofuser, nav_emailofuser, visibleDonors);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.visibleDonors){
            if (item.isChecked()){
                item.setChecked(false);
                updateVisible(false);
            } else {
                item.setChecked(true);
                updateVisible(true);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void updateVisible(boolean b) {
        HashMap<String, Object> updateValues = new HashMap<>();

        if(b){
            updateValues.put("Visible","True");
        }else {
            updateValues.put("Visible","False");
        }
        FirebaseDatabase.getInstance("https://blood-bank-management-306c0-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Donors").child(uid).updateChildren(updateValues);
    }

    private void updateNameEmailAndCheckBox(TextView nav_nameofuser, TextView nav_emailofuser, MenuItem visibleDonors) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://blood-bank-management-306c0-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference ref = database.getReference("Donors");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);

                    // Getting the current user's details
                    if (user.getUID().equals(uid)) {
                        self = user;

                        // If user requests for blood then that user will be removed from available donors
                        if (self.getRequestBlood().equals("True")){
                            visibleDonors.setChecked(false);
                            visibleDonors.setEnabled(false);
                            updateVisible(false);
                        } else{
                            visibleDonors.setEnabled(true);
                        }

                        // Setting visible as donor checkbox
                        visibleDonors.setChecked(self.getVisible().equals("True"));

                        // Setting name of user and email in drawer
                        nav_nameofuser.setText(self.getFName() + " " +self.getLName());
                        nav_emailofuser.setText(self.getEmail());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}