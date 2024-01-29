package com.example.foodhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.example.foodhub.R;


public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        ImageView imageView2 = findViewById(R.id.imageView2);
        imageView2.setImageResource(R.drawable.home_orange);

        //Google



        GoogleSignInAccount googleUser = GoogleSignIn.getLastSignedInAccount(this);

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

                    }else if(googleUser != null){
                        welcomeUser.setText("Welcome " + googleUser.getDisplayName());
                        String photoUrl = googleUser.getPhotoUrl() != null
                                ? googleUser.getPhotoUrl().toString()
                                : null;
                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            Glide.with(HomeActivity.this)
                                    .load(photoUrl)
                                    .circleCrop()
                                    .into(avatar);
                        }else{
                            avatar.setImageResource(R.mipmap.avatar);

                        }
                    }
                    else{
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

        SearchView searchView = findViewById(R.id.searchView);

        int searchIconId = searchView.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView searchIcon = searchView.findViewById(searchIconId);
        searchIcon.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

    }

    private void startLogoutActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void accountSettings(View view){
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    public void menuView(View view) {
        Intent intent = new Intent(HomeActivity.this, MenuActivity.class
        );
        startActivity(intent);
    }

    public void cartView(View view) {
        Intent intent = new Intent(HomeActivity.this, CartActivity.class
        );
        startActivity(intent);
    }

}