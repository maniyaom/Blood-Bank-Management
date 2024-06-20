package com.example.bloodbankmanagement;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    TextInputEditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = findViewById(R.id.emailResetPassword);
    }

    public void resetPasswordNow(View view){
        if (email.getText().toString().isEmpty()){
            email.setError("Fill this field.");
        } else{
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"Password Reset Link Sent On Registered Email.", Snackbar.LENGTH_LONG);
            View view1 = snack.getView();
            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view1.getLayoutParams();
            params.gravity = Gravity.CENTER_VERTICAL;
            view1.setLayoutParams(params);
            FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString());
            snack.show();
        }
    }
}