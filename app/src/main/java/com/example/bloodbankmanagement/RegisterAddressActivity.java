package com.example.bloodbankmanagement;

// This is the second step of the registration process.
// This activity is used to get the address of the donor.

// Importing all the required packages
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

// Creating a class for RegisterAddressActivity
public class RegisterAddressActivity extends AppCompatActivity {

    // Declaring all the required variables
    AutoCompleteTextView states;
    TextInputEditText District,Town, Pincode;
    Button nextToBlood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_address);

        // Initializing all the variables
        initializeComponents();


        // Setting the adapter for the state drop down
        states.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.states)));
    }


    // Initializing all the variables
    private void initializeComponents() {
        District = findViewById(R.id.districtRegister);
        Town = findViewById(R.id.townRegister);
        Pincode = findViewById(R.id.pincodeRegister);
        states = findViewById(R.id.stateDropDrown);
        nextToBlood = findViewById(R.id.nextButtonII);
    }

    // This method is called when the submit button is clicked
    public void registerBlood(View view) {

        // Getting the values from the text fields
        String districtT,townT,pincodeT,stateT;
        districtT = District.getText().toString();
        townT = Town.getText().toString();
        pincodeT = Pincode.getText().toString();
        stateT = states.getText().toString();

        // Checking if the input fields are empty or not
        if(!districtT.isEmpty() && !townT.isEmpty() && !pincodeT.isEmpty() && !stateT.isEmpty() && !stateT.equalsIgnoreCase("State")){
            addDataToFirebaseStorage(stateT,districtT,townT,pincodeT,FirebaseAuth.getInstance().getUid());
        }
        if(stateT.isEmpty() || stateT.equalsIgnoreCase("state")){
            states.setError("Fill this field.");
        }
        if(districtT.isEmpty()){
            District.setError("Fill this field.");
        }
        if(townT.isEmpty()){
            Town.setError("Fill this field.");
        }
        if(pincodeT.isEmpty()){
            Pincode.setError("Fill this field.");
        }

        // Starting the next activity
        startActivity(new Intent(RegisterAddressActivity.this, RegisterBloodActivity.class));

    }

    // This method is used to add the data to the firebase database
    private void addDataToFirebaseStorage(String stateT, String districtT, String townT, String pincodeT, String uid) {

        // Creating a hashmap to store the data
        HashMap<String,Object> values = new HashMap<>();
        values.put("State",stateT);
        values.put("District",districtT);
        values.put("Town",townT);
        values.put("Pincode",pincodeT);
        values.put("Step","2");

        // Updating the data in the firebase database
        FirebaseDatabase.getInstance("https://blood-bank-management-306c0-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Donors")
                .child(uid).updateChildren(values);
    }
}