package com.example.foodhub.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.foodhub.models.Cart;
import java.util.ArrayList;
import java.util.List;
import com.example.foodhub.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Cart> cartItemList;
    private DatabaseReference menuRef;

    public CartAdapter(List<Cart> cartItemList,DatabaseReference menuRef) {
        this.cartItemList = cartItemList;
        this.menuRef = menuRef;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cartItem = cartItemList.get(position);
        final int itemPosition = position;
        holder.textViewItemName.setText(cartItem.getName());
        holder.textViewItemPrice.setText(cartItem.getPrice());
        Glide.with(holder.itemView.getContext())
                .load(cartItem.getImage())
                .into(holder.imageView);

        if (holder.deleteCartItems != null) {
            holder.deleteCartItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteCartItem(cartItem.getCartId());
                }
            });
        }
    }






    public void  deleteCartItem(String cartItemId) {
        if (menuRef != null) {
            menuRef.child(cartItemId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            menuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    List<Cart> updatedCartItemList = new ArrayList<>();

                                    for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                                        Cart cartItem = cartSnapshot.getValue(Cart.class);
                                        if (cartItem != null) {
                                            updatedCartItemList.add(cartItem);
                                        }
                                    }
                                    updateData(updatedCartItemList);
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

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItemName;
        TextView textViewItemPrice;
        ImageView imageView;
        ImageView deleteCartItems;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.textViewName);
            textViewItemPrice = itemView.findViewById(R.id.textViewPrice);
            imageView = itemView.findViewById(R.id.imageView);
            deleteCartItems = itemView.findViewById(R.id.openMenuImage);

        }
    }

    public void updateData(List<Cart> newCartItemList) {
        this.cartItemList = newCartItemList;
        notifyDataSetChanged();
    }
}