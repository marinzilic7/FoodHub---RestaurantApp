package com.example.foodhub;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.foodhub.R;

public class NewPasswordActivity extends AppCompatActivity {
    FirebaseAuth auth;
    EditText oldPassword;
    EditText newPassword;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_password);

        auth = FirebaseAuth.getInstance();

        newPassword= findViewById(R.id.newPassword);
        confirmButton = findViewById(R.id.confirmButton);
        oldPassword = findViewById(R.id.oldPassword);

        confirmButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword(){
        String old_password = oldPassword.getText().toString();
        String new_Password = newPassword.getText().toString();

        if(old_password.isEmpty()){
            oldPassword.setError("Old password is required");
            oldPassword.requestFocus();
            return;
        }
        if(new_Password.isEmpty()){
            newPassword.setError("New password is required");
            newPassword.requestFocus();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            auth.signInWithEmailAndPassword(user.getEmail(), old_password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(new_Password)
                                    .addOnCompleteListener(passwordUpdateTask -> {
                                        if (passwordUpdateTask.isSuccessful()) {
                                            Toast.makeText(NewPasswordActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(NewPasswordActivity.this, "Change failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(NewPasswordActivity.this, "That is not your old password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}