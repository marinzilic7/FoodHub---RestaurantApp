package com.example.foodhub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.foodhub.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth; //Firebase Authentication instance
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        firebaseAuth = FirebaseAuth.getInstance(); //Firebase Authentication instance

        //By signing up you accept the Team of service and Privacy Policy
        CheckBox checkBox = findViewById(R.id.checkedTextView);
        String checkBoxText = "By signing up you accept the Team of service and Privacy Policy";
        SpannableStringBuilder spannable = new SpannableStringBuilder(checkBoxText);
        spannable.setSpan(
                new ForegroundColorSpan(Color.parseColor("#FF5722")),
                29,
                44,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE

        );
        spannable.setSpan(
                new ForegroundColorSpan(Color.parseColor("#FF5722")),
                48,
                63,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE

        );
        checkBox.setText(spannable);
    }

    public void registerUser(View view) {
        EditText firstName = findViewById(R.id.firstName);
        EditText lastName = findViewById(R.id.lastName);
        EditText email = findViewById(R.id.userEmail);
        EditText password = findViewById(R.id.userPassword);
        RadioGroup radioGroupGender = findViewById(R.id.radioGroupGender);
        RadioButton maleRadioButton = findViewById(R.id.radioButtonMale);
        RadioButton femaleRadioButton = findViewById(R.id.radioButtonFemale);
        CheckBox isChecked = findViewById(R.id.checkedTextView);

        String firstNameText = firstName.getText().toString();
        String lastNameText = lastName.getText().toString();
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();


        if(firstNameText.isEmpty()) {
            firstName.setError("First name is required");
            firstName.requestFocus();
            return;
        }

        if(lastNameText.isEmpty()) {
            lastName.setError("Last name is required");
            lastName.requestFocus();
            return;
        }

        if(emailText.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if(passwordText.length() < 6) {
            password.setError("Password should be at least 6 characters long");
            password.requestFocus();
            return;
        }

        radioGroupGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonMale) {
                femaleRadioButton.setChecked(false); // Ako je odabrano muško, postavljamo žensko kao neodabrano
            } else if (checkedId == R.id.radioButtonFemale) {
                maleRadioButton.setChecked(false); // Ako je odabrano žensko, postavljamo muško kao neodabrano
            }
        });



        if (!maleRadioButton.isChecked() && !femaleRadioButton.isChecked()) {
            Toast.makeText(RegisterActivity.this, "Select your gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isChecked.isChecked()){
            Toast.makeText(this, "Please accept the terms of use", Toast.LENGTH_SHORT).show();
        }else{
            firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String gender = maleRadioButton.isChecked() ? "Male" : "Female";
                            // Create a User object and store additional information
                            User user = new User(firstNameText, lastNameText, emailText, gender);
                            String userId = firebaseUser.getUid();
                            // Store the user in the Firebase Realtime Database
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                            databaseReference.child(firebaseUser.getUid()).setValue(user);

                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMessageFromLog = task.getException().getMessage();
                            Log.e("RegisterActivity", "Registration failed: " + task.getException().getMessage());
                            Toast.makeText(RegisterActivity.this, errorMessageFromLog, Toast.LENGTH_SHORT).show();

                        }
                    });
        }




    }

    public void startLoginActivity(View view ) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}