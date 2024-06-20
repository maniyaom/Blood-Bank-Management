package com.example.bloodbankmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

// Creating a class for RegisterBloodActivity
public class RegisterBloodActivity extends AppCompatActivity {

    // Declaring all the required variables
    AutoCompleteTextView bloodgrp;
    TextInputEditText mobile,textVerification;
    Button submit;

    // Declaring all the required boolean variables, and a string variable
    boolean isVerified = false, isSubmit = false;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_blood);

        // Initializing all the variables
        initializeComponents();

        // Creating an array for the blood group drop down
        String[] bloodGroups = getResources().getStringArray(R.array.blood_groups);

        // Creating an adapter for the blood group drop down
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,bloodGroups);

        // Setting the adapter for the blood group drop down
        bloodgrp.setAdapter(adapter);
    }

    // Method to initialize all the variables
    private void initializeComponents() {
        bloodgrp = findViewById(R.id.bloodGrpDropDown);
        mobile = findViewById(R.id.mobileEditText);
        textVerification = findViewById(R.id.verificationText);
        submit = findViewById(R.id.btnVerifySubmit);
    }

    // This method is called when the submit button is clicked
  ````-  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // This method is called when the verification is completed
        @SuppressLint("SetTextI18n")
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {

            // Setting the text of the textVerification to "Verified ! ✔" if the verification is completed
            textVerification.setText("Verified ! ✔");
            addToDatabase();
        }

        // This method is called when the verification fails
        @Override
        public void onVerificationFailed(FirebaseException e) {

            // Setting the text of the textVerification to "Failed!" if the verification fails
            // Setting the text of the textVerification to "Message Quota Exceeded!\nTry Again After few Hours!" if the message quota is exceeded
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                textVerification.setText("Failed!");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                textVerification.setText("Message Quota Exceeded!\nTry Again After few Hours!");
            }
            mobile.setEnabled(true);

        }

        // This method is called when the code/OTP is sent
        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {

            // Setting the text of the textVerification to "Enter OTP!" if the code/OTP is sent
            textVerification.setHint("Enter OTP!");
            submit.setText("Submit");
            id = verificationId;
            isSubmit = true;

        }

    };

    // Method to add the user to the database
    private void addToDatabase() {

        // Creating a HashMap to store the values
        HashMap<String,Object> values = new HashMap<>();
        values.put("Step","Done");
        values.put("Mobile",mobile.getText().toString());
        values.put("BloodGroup",bloodgrp.getText().toString());
        values.put("Visible","True");

        // Updating the database
        FirebaseDatabase.getInstance("https://blood-bank-management-306c0-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Donors/"+FirebaseAuth.getInstance().getUid())
                .updateChildren(values)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    // This method is called when the database is updated
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            // Starting the DisplayRequestsActivity if the database is updated successfully
                            Intent intent = new Intent(RegisterBloodActivity.this, NavigationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        }else {
                            Toast.makeText(RegisterBloodActivity.this, "Error while register !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    // This method is called when the submit button is clicked
    public void verifyAndSubmit(View view) {

        // Disabling the mobile number EditText if the verification is not completed
        mobile.setEnabled(false);
        if(!isSubmit) {

            // Verifying the mobile number if the verification is not completed
            if (!isVerified && !mobile.getText().toString().isEmpty() && !bloodgrp.getText().toString().isEmpty()) {
                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber("+91" + mobile.getText().toString())
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(RegisterBloodActivity.this)
                        .setCallbacks(mCallbacks)
                        .build();

                // Setting the text of the textVerification to "Verifying..." if the verification is not completed
                PhoneAuthProvider.verifyPhoneNumber(options);
                textVerification.setHint("Verifying...");
            }

            // Setting the error if the mobile number is empty
            if (mobile.getText().toString().isEmpty()) {
                mobile.setError("Enter Mobile Number!");
            }
        }else {

            // Verifying the OTP if the verification is completed
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id,textVerification.getText().toString());
            FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        // This method is called when the OTP is verified
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                // Setting the text of the textVerification to "Verified ! ✔" if the OTP is verified
                                addToDatabase();
                            }else {

                                // Setting the text of the textVerification to "Not Verified!" if the OTP is not verified
                                Toast.makeText(RegisterBloodActivity.this, "Error!\n"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                submit.setText("Verify");
                                mobile.setEnabled(true);
                                textVerification.setHint("Not Verified!");
                                isVerified = false;
                                isSubmit = false;
                            }
                        }
                    });
        }

    }
}
