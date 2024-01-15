package com.example.foodhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;




public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        ImageView avatar = findViewById(R.id.avatarWelcome);


        TextView welcomeUser = findViewById(R.id.welcomeUser);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference base = FirebaseDatabase.getInstance().getReference("users");
        if(user != null){
            String userId = user.getUid();

            base.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String gender = snapshot.child("gender").getValue(String.class);
                        String firstName = snapshot.child("firstName").getValue(String.class);
                        String lastName = snapshot.child("lastName").getValue(String.class);
                        welcomeUser.setText("Welcome " + firstName + " " + lastName);
                        if ("male".equalsIgnoreCase(gender)) {
                            avatar.setImageResource(R.mipmap.avatar);
                        } else {
                            avatar.setImageResource(R.mipmap.girl_avatar);
                        }

                    }else{
                        startLogoutActivity();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    startLogoutActivity();
                }
            });
        }else{
            startLogoutActivity();
        }

    }

    private void startLogoutActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void LogoutBtn(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}