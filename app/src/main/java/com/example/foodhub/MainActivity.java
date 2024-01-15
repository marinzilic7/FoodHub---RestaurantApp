package com.example.foodhub;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;




public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        EditText userEmail = findViewById(R.id.userEmail);
        EditText userPassword = findViewById(R.id.userPassword);
        CheckBox rememberMe = findViewById(R.id.rememberMe);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedEmail = preferences.getString("email", null);
        String savedPassword = preferences.getString("password", null);

        if (savedEmail != null && savedPassword != null) {

            userEmail.setText(savedEmail);
            userPassword.setText(savedPassword);
            rememberMe.setChecked(true);
        }
    }


    private void loginUser(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        CheckBox rememberMe = findViewById(R.id.rememberMe);

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    String savedEmail = preferences.getString("email", null);
                    String savedPassword = preferences.getString("password", null);
                    if (rememberMe.isChecked()) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("email", savedEmail);
                        editor.putString("password", savedPassword);
                        editor.apply();
                    }
                    FirebaseUser user = auth.getCurrentUser();

                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    String errorMessage = task.getException().getMessage();
                    Log.e("LoginError", errorMessage);
                    Toast.makeText(MainActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void startLogin(View view) {
        EditText userEmail = findViewById(R.id.userEmail);
        EditText userPassword = findViewById(R.id.userPassword);

        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (email.isEmpty()) {
            userEmail.setError("Please enter your email");
            userEmail.requestFocus();
        } else if (password.isEmpty()) {
            userPassword.setError("Please enter your password");
            userPassword.requestFocus();
        } else if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
        } else if (!(email.isEmpty() && password.isEmpty())) {
            loginUser(email, password);
        } else {
            Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();

        }


    }

    public void startRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
