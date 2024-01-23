package com.example.foodhub.adapters;

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
    public MenuAdapter(List<Menu> menuList,DatabaseReference menuRef) {
        this.menuList = menuList;
        this.menuRef = menuRef;
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
                    deleteMeal(menuList.get(position).getMealId());
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
                                    }
                                    updateData(updatedMenuList);
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
                            Log.e("Cart", "Dodano je " + itemName + " u košaricu");
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
