package com.example.foodhub.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.foodhub.R;
import com.example.foodhub.models.Cart;
import com.example.foodhub.models.Menu;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private int selectedPosition = RecyclerView.NO_POSITION;
    private List<Menu> menuList;
    private DatabaseReference menuRef;

    private GoogleSignInAccount googleUser;
    public MenuAdapter(List<Menu> menuList,DatabaseReference menuRef,GoogleSignInAccount googleUser) {
        this.menuList = menuList;
        this.menuRef = menuRef;
        this.googleUser = googleUser;
    }




    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        final MenuViewHolder viewHolder = new MenuViewHolder(view);
        return viewHolder;
    }

    public void updateData(List<Menu> newMenuList) {
        this.menuList = newMenuList;
        notifyDataSetChanged();
        Log.d("MenuAdapter", "Data updated. New list size: " + newMenuList.size());
    }

    public MenuAdapter(GoogleSignInAccount googleUser) {
        this.googleUser = googleUser;
    }

    public void updateDataFood(List<Menu> newMenuList, String categoryToUpdate) {
        List<Menu> filteredList = new ArrayList<>();

        for (Menu menu : newMenuList) {
            if ("food".equals(menu.getCategory())) {
                filteredList.add(menu);
            }
        }

        this.menuList = filteredList;
        notifyDataSetChanged();
    }



    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Menu menu = menuList.get(position);
        final int itemPosition = position;
        holder.textViewName.setText(menu.getName());
        holder.textViewPrice.setText(menu.getPrice());

        if (menu.getImage() != null && !menu.getImage().isEmpty()) {
            ImageView imageView = holder.itemView.findViewById(R.id.imageView);
            Glide.with(holder.itemView.getContext())
                    .load(menu.getImage())
                    .into(imageView);

        }

        holder.openMenuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, itemPosition);
            }
        });




    }

    public boolean isUserSignedInWithGoogle() {
        return googleUser != null;
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.menu_item_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();

                final int cart = R.id.cart;
                final int delete= R.id.delete;

                if (id == delete) {
                    boolean isUserSignedIn = isUserSignedInWithGoogle();

                    if (isUserSignedIn) {
                        Toast.makeText(view.getContext(), "User is signed in with Google", Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference base = FirebaseDatabase.getInstance().getReference("users");
                        if (user != null) {
                            String userId = user.getUid();
                            base.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String userRole = snapshot.child("role").getValue(String.class);
                                        if ("administrator".equalsIgnoreCase(userRole)) {
                                            deleteMeal(menuList.get(position).getMealId());
                                        }else{
                                            Toast.makeText(view.getContext(), "You are not administrator", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(view.getContext(), "You are not administrator", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }


                }else if(id == cart){
                    addToCart(menuList.get(position));
                }
                return true;
            }
        });

        popupMenu.show();
    }


    @Override
    public int getItemCount() {
        return menuList.size();
    }

    private void updateDataFood(List<Menu> newMenuList) {
        List<Menu> filteredList = new ArrayList<>();

        for (Menu menu : newMenuList) {
            if ("food".equals(menu.getCategory())) {
                filteredList.add(menu);
            }
        }

        this.menuList = filteredList;
        notifyDataSetChanged();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewPrice;
        ImageView openMenuImage;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            openMenuImage = itemView.findViewById(R.id.openMenuImage);
        }
    }




    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView openMenuImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            openMenuImage = itemView.findViewById(R.id.openMenuImage);
        }
    }

    private void deleteMeal(String mealId) {

        if (menuRef != null) {
            menuRef.child(mealId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            menuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    List<Menu> updatedMenuList = new ArrayList<>();

                                    for (DataSnapshot menuSnapshot : dataSnapshot.getChildren()) {
                                        Menu menu = menuSnapshot.getValue(Menu.class);


                                        if (menu != null) {
                                            updatedMenuList.add(menu);
                                        }
                                        updateDataFood(updatedMenuList);
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
    }

    private void addToCart(Menu menu) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId);
            String itemId = menu.getMealId();
            String itemName = menu.getName();
            String itemPrice = menu.getPrice();
            String imageUrl = menu.getImage();
            Cart cartItem = new Cart(itemId, itemName, itemPrice, imageUrl);
            String path = cartRef.child(itemId).toString();
            cartRef.child(itemId).setValue(cartItem)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Cart", "Greška prilikom dodavanja u košaricu: " + e.getMessage());
                        }
                    });
        }
    }

}
