package com.example.foodhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText userEmail;
    private EditText firstNameText;
    private EditText lastNameText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
        auth = FirebaseAuth.getInstance();

        ImageView imageView5 = findViewById(R.id.imageView5);
        imageView5.setImageResource(R.drawable.account_orange);

        userEmail = findViewById(R.id.emailUser);
        firstNameText = findViewById(R.id.firstName);
        lastNameText = findViewById(R.id.lastName);
        ImageView avatar = findViewById(R.id.profile);


        GoogleSignInAccount googleUser = GoogleSignIn.getLastSignedInAccount(this);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        if (user != null) {
            String userId = user.getUid();

            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String gender = snapshot.child("gender").getValue(String.class);
                        String firstName = snapshot.child("firstName").getValue(String.class);
                        String lastName = snapshot.child("lastName").getValue(String.class);
                        userEmail.setText(user.getEmail());
                        firstNameText.setText(firstName);
                        lastNameText.setText(lastName);
                        if ("male".equalsIgnoreCase(gender)) {
                            avatar.setImageResource(R.mipmap.avatar);
                        } else {
                            avatar.setImageResource(R.mipmap.girl_avatar);
                        }
                    }else if(googleUser != null){
                        firstNameText.setText(googleUser.getGivenName());
                        lastNameText.setText(googleUser.getFamilyName());
                        userEmail.setText(googleUser.getEmail());
                        String photoUrl = googleUser.getPhotoUrl() != null
                                ? googleUser.getPhotoUrl().toString()
                                : null;
                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            Glide.with(AccountActivity.this)
                                    .load(photoUrl)
                                    .circleCrop()
                                    .into(avatar);
                        }else{
                            avatar.setImageResource(R.mipmap.avatar);

                        }
                    } else {
                        Toast.makeText(AccountActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        ImageView dots = findViewById(R.id.imageDots);

        dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

    }


    public void showPopupMenu(View view) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.getMenuInflater().inflate(R.menu.android_menu, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                final int PASSWORD_ID = R.id.change_password;
                final int LOGOUT_ID = R.id.logout;
                if (id == PASSWORD_ID) {
                    changePassword();
                }else if(id == LOGOUT_ID){
                    odjava(view);
                }

                return true;
            }

            ;
        });

        menu.show();
    }

    public void odjava(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    auth.signOut();
                    odjava(); //vraca na login
                }
            });
        } else {
            auth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void odjava() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



    public void homeView(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void changePassword(){
        Intent intent = new Intent(this, NewPasswordActivity.class);
        startActivity(intent);
    }

    public void menuView(View view) {
        Intent intent = new Intent(AccountActivity.this, MenuActivity.class
        );
        startActivity(intent);
    }

    public void cartView(View view) {
        Intent intent = new Intent(AccountActivity.this, CartActivity.class
        );
        startActivity(intent);
    }


}
