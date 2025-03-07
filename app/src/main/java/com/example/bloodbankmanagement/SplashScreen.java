package com.example.bloodbankmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new CountDownTimer(1000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if(user != null){
                    getSelf();
                }else {
                    startActivity(new Intent(getApplicationContext(),LoginScreenActivity.class));
                    SplashScreen.this.finish();
                }

            }
        }.start();
 }

    private void getSelf() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://blood-bank-management-306c0-default-rtdb.asia-southeast1.firebasedatabase.app/");

        database.getReference("Donors/"+user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User users = snapshot.getValue(User.class);

                        switch (users.getStep()){
                            case "1": startActivity(new Intent(SplashScreen.this, RegisterAddressActivity.class));
                                break;
                            case "2": startActivity(new Intent(SplashScreen.this, RegisterBloodActivity.class));
                                break;
                            case "Done": startActivity(new Intent(SplashScreen.this, NavigationActivity.class));
                                break;
                        }
                        SplashScreen.this.finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}
