package com.example.foodhub;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhub.adapters.CartAdapter;
import com.example.foodhub.models.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private DatabaseReference cartRef;
    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private TextView textViewCartEmpty;
    private ImageView imageViewCartEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_layout);

        recyclerViewCart = findViewById(R.id.recyclerViewMenu);
        textViewCartEmpty = findViewById(R.id.textViewCartEmpty);
        imageViewCartEmpty = findViewById(R.id.imageViewCartEmpty);

        ImageView imageView4 = findViewById(R.id.imageView4);
        imageView4.setImageResource(R.drawable.order_orange);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId);

            recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
            cartAdapter = new CartAdapter(new ArrayList<>(), cartRef);
            recyclerViewCart.setAdapter(cartAdapter);

            cartRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Cart> updatedCartItemList = new ArrayList<>();

                    for (DataSnapshot cartItemSnapshot : dataSnapshot.getChildren()) {
                        Cart cartItem = cartItemSnapshot.getValue(Cart.class);
                        if (cartItem != null) {
                            updatedCartItemList.add(cartItem);
                        }
                    }

                    cartAdapter.updateData(updatedCartItemList);

                    if (updatedCartItemList.isEmpty()) {
                        recyclerViewCart.setVisibility(View.GONE);
                        textViewCartEmpty.setVisibility(View.VISIBLE);
                        imageViewCartEmpty.setVisibility(View.VISIBLE);
                    } else {
                        recyclerViewCart.setVisibility(View.VISIBLE);
                        textViewCartEmpty.setVisibility(View.GONE);
                        imageViewCartEmpty.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("CartItem", "Error fetching cart data: " + databaseError.getMessage());
                }
            });
        }
    }

    public void menuView(View view) {
        Intent intent = new Intent(CartActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    public void accountSettings(View view) {
        Intent intent = new Intent(CartActivity.this, AccountActivity.class);
        startActivity(intent);
    }

    public void homeView(View view) {
        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}