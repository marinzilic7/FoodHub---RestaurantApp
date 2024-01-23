package com.example.foodhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_layout);

        recyclerViewCart = findViewById(R.id.recyclerViewMenu);
        textViewCartEmpty = findViewById(R.id.textViewCartEmpty);

        // Firebase initialization
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId);

            // Initialize RecyclerView and Adapter
            recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
            cartAdapter = new CartAdapter(new ArrayList<>(), cartRef);
            recyclerViewCart.setAdapter(cartAdapter);

            // Listen for changes in the cart
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

                    // Update the adapter with the new list of items
                    cartAdapter.updateData(updatedCartItemList);

                    // Show/hide "Your cart is empty" message based on the data
                    if (updatedCartItemList.isEmpty()) {
                        recyclerViewCart.setVisibility(View.GONE);
                        textViewCartEmpty.setVisibility(View.VISIBLE);
                    } else {
                        recyclerViewCart.setVisibility(View.VISIBLE);
                        textViewCartEmpty.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("CartItem", "Error fetching cart data: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("CartItem", "User not logged in");
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