package com.example.foodhub;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.foodhub.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText sentEmail;
    ImageView googleBtn;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 1001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);



        googleBtn = findViewById(R.id.googleBtn);
        GoogleSignInAccount googleUser = GoogleSignIn.getLastSignedInAccount(this);
        if (googleUser != null) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);

        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("978159753139-p6djuobnsrqh2cijkv5nnjbtpo2traf1.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mGoogleSignInClient.signOut();  // Odjava svih naloga pre nego što pokaže prozor za izbor naloga
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        auth = FirebaseAuth.getInstance();

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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google sign in failed
                Log.w("GoogleSignIn", "Google sign in failed: " + e.getStatusCode());
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirebaseAuth", "signInWithCredential:success");
                        Toast.makeText(MainActivity.this, "Firebase authentication successful", Toast.LENGTH_SHORT).show();
                        navigateToSecondActivity();
                    } else {
                        Log.w("FirebaseAuth", "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Firebase authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void navigateToSecondActivity(){
        finish();
        Intent intent = new Intent(MainActivity.this, HomeActivity.class
        );
        startActivity(intent);
    }

    private void loginUser(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        CheckBox rememberMe = findViewById(R.id.rememberMe);

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    preferences.edit().clear().apply(); //fixed
                    String savedEmail = preferences.getString("email", null);
                    String savedPassword = preferences.getString("password", null);
                    if (rememberMe.isChecked()) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("email", email);
                        editor.putString("password", password);
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

    public void setNewPassword(View view){
        openModal();
    }

    private void openModal(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.password_modal, null);
        builder.setView(dialogView);

        sentEmail = dialogView.findViewById(R.id.userEmail);
        Button sendEmailButton = dialogView.findViewById(R.id.sendEmailButton);

       sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword(v);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void resetPassword(View view){

        String emailUser = sentEmail.getText().toString();

        if(emailUser.isEmpty()){
            sentEmail.setError("Type your email");
        }else{
            auth.sendPasswordResetEmail(emailUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Email sent" , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Greška", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }



}
