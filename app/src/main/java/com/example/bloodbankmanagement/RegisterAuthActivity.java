package com.example.bloodbankmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


// Creating a class for RegisterAuthActivity
public class RegisterAuthActivity extends AppCompatActivity {

//    ActivityMainBinding binding;
    // Declaring all the required variables
    com.google.android.material.textfield.TextInputEditText fName,lName,email,pass, confirmPass;
    Button nextToAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_register_auth);


        // Initializing all the variables
        initializeComponents();
    }

    private void initializeComponents() {

        // Initializing all the variables
        fName = findViewById(R.id.fNameInput);
        lName = findViewById(R.id.lNameInput);
        email = findViewById(R.id.emailInput);
        pass = findViewById(R.id.createPassInput);
        confirmPass = findViewById(R.id.confirmPassInput);
        nextToAuth = findViewById(R.id.nextButtonI);

    }

    public void nextRegisterPage(View view) {

        // Getting the values from the input fields
        String f_name,l_name,emailText,passText, confirmPassText;
        f_name = fName.getText().toString();
        l_name = lName.getText().toString();
        emailText = email.getText().toString().toLowerCase();
        passText = pass.getText().toString();
        confirmPassText = confirmPass.getText().toString();

        // Checking if the input fields are empty or not
        if(f_name.isEmpty()){
            fName.setError("Fill this field.");
        }
        if(emailText.isEmpty()){
            email.setError("Fill this field.");
        }
        if (l_name.isEmpty()){
            lName.setError("Fill this field.");
        }
        if(passText.isEmpty()){
            pass.setError("Fill this field.");
        }
        if(confirmPassText.isEmpty()){
            confirmPass.setError("Fill this field.");
        }
        else{
            if (!passText.equals(confirmPassText)){
                Toast.makeText(getApplicationContext(), "Passwords are not matching", Toast.LENGTH_SHORT).show();
            }
            else{
                RegisterUser(f_name,l_name,emailText,passText);
            }
        }

    }

    // Registering the user using FirebaseAuth
    private void RegisterUser(String f_name, String l_name, String emailText, String passText) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText,passText)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // If the user is registered successfully, then add the user to the database
                            addToDatabase(task.getResult().getUser().getUid(),f_name,l_name,emailText);
                        }else {
                            Toast.makeText(RegisterAuthActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Adding the user to the database
    private void addToDatabase(String uid, String f_name, String l_name, String emailText) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://blood-bank-management-306c0-default-rtdb.asia-southeast1.firebasedatabase.app/");

        HashMap<String,String>values = new HashMap<>();
        values.put("FName",f_name);
        values.put("LName",l_name);
        values.put("Email",emailText);
        values.put("UID",uid);
        values.put("Step","1");
        values.put("Visible","False");
        values.put("RequestBlood","False");
        values.put("State","None");
        values.put("District","None");
        values.put("Mobile","None");
        values.put("BloodGroup","None");

        // Adding the user to the database
        database.getReference("Donors")
                .child(uid).setValue(values).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        startActivity(new Intent(RegisterAuthActivity.this, RegisterAddressActivity.class));
                    }
                });
    }
}