package com.example.bloodbankmanagement;

// This is the Login Screen Activity

// Importing all the required packages
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;



// Creating a class for LoginScreenActivity
public class LoginScreenActivity extends AppCompatActivity {

    // Declaring all the required variables
    TextInputEditText Email,Pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Initializing all the variables

        Email = findViewById(R.id.emailLogin);
        Pass = findViewById(R.id.passLogin);
    }

    // Creating a method for opening the RegisterAuthActivity
    // This method will be called when the user clicks on the "Register" button
    public void OpenRegisterActivity(View view) {
        startActivity(new Intent(LoginScreenActivity.this, RegisterAuthActivity.class));
    }

    // Creating a method for logging in the user
    public void LoginNow(View view) {

        // Checking if the email and password fields are empty

        if(!Email.getText().toString().isEmpty() && !Pass.getText().toString().isEmpty()){

            // Signing in the user
            FirebaseAuth.getInstance().signInWithEmailAndPassword(Email.getText().toString(),Pass.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override

                        // Checking if the user is signed in or not
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                // If the user is signed in, then the user will be redirected to the SplashScreen
                                startActivity(new Intent(LoginScreenActivity.this,SplashScreen.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Incorrect email id or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(LoginScreenActivity.this, "Enter your email and password", Toast.LENGTH_SHORT).show();
        }

    }

    public void gotoResetPasswordPage(View view){
        Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
        startActivity(intent);
    }
}