package com.example.foodhub;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhub.adapters.MenuAdapter;
import com.example.foodhub.models.Menu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MenuActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri; // Dodajte ovu liniju
    private RecyclerView recyclerView;
    private TextView drinkBtn;
    private TextView desertBtn;
    private TextView otherBtn;
    private TextView foodBtn;
    private MenuAdapter menuAdapter;
    private TextView textViewMenuEmpty;
    private ImageView imageViewMenuEmpty;

    private List<Menu> menuList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        drinkBtn = findViewById(R.id.drinkBtn);
        desertBtn = findViewById(R.id.desertBtn);
        otherBtn = findViewById(R.id.otherBtn);
        foodBtn = findViewById(R.id.foodBtn);
        textViewMenuEmpty = findViewById(R.id.textViewMenuEmpty);
        imageViewMenuEmpty = findViewById(R.id.imageViewMenuEmpty);


        ImageView imageView2 = findViewById(R.id.imageView3);
        imageView2.setImageResource(R.drawable.heart_orange);

        ImageView addItemIcon = findViewById(R.id.addItem);

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
                            addItemIcon.setImageResource(R.mipmap.add);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



        RecyclerView recyclerView = findViewById(R.id.recyclerViewMenu);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("menu");


        menuAdapter = new MenuAdapter(menuList, menuRef);
        recyclerView.setAdapter(menuAdapter);

        menuRef.orderByChild("category").equalTo("food").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Menu> filteredMenuList = new ArrayList<>();

                for (DataSnapshot menuSnapshot : dataSnapshot.getChildren()) {
                    Menu menu = menuSnapshot.getValue(Menu.class);
                    if (menu != null) {
                        filteredMenuList.add(menu);
                        menuAdapter.notifyDataSetChanged();
                    }
                }

                RecyclerView recyclerView = findViewById(R.id.recyclerViewMenu);
                MenuAdapter menuAdapter = new MenuAdapter(filteredMenuList,menuRef);
                recyclerView.setAdapter(menuAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        drinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("menu");
                menuRef.orderByChild("category").equalTo("drinks").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Menu> filteredMenuList = new ArrayList<>();

                        for (DataSnapshot menuSnapshot : dataSnapshot.getChildren()) {
                            Menu menu = menuSnapshot.getValue(Menu.class);
                            if (menu != null) {
                                filteredMenuList.add(menu);
                            }
                        }
                        RecyclerView recyclerView = findViewById(R.id.recyclerViewMenu);
                        MenuAdapter menuAdapter = new MenuAdapter(filteredMenuList,menuRef);
                        recyclerView.setAdapter(menuAdapter);

                        menuAdapter.updateData(filteredMenuList);

                        if (filteredMenuList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            textViewMenuEmpty.setVisibility(View.VISIBLE);
                            imageViewMenuEmpty.setVisibility(View.VISIBLE);

                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            textViewMenuEmpty.setVisibility(View.GONE);
                            imageViewMenuEmpty.setVisibility(View.GONE);
                        }
                    }





                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        desertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("menu");
                menuRef.orderByChild("category").equalTo("desert").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Menu> filteredMenuList = new ArrayList<>();

                        for (DataSnapshot menuSnapshot : dataSnapshot.getChildren()) {
                            Menu menu = menuSnapshot.getValue(Menu.class);
                            if (menu != null) {
                                filteredMenuList.add(menu);
                            }
                        }
                        RecyclerView recyclerView = findViewById(R.id.recyclerViewMenu);
                        MenuAdapter menuAdapter = new MenuAdapter(filteredMenuList,menuRef);
                        recyclerView.setAdapter(menuAdapter);

                        menuAdapter.updateData(filteredMenuList);

                        if (filteredMenuList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            textViewMenuEmpty.setVisibility(View.VISIBLE);
                            imageViewMenuEmpty.setVisibility(View.VISIBLE);

                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            textViewMenuEmpty.setVisibility(View.GONE);
                            imageViewMenuEmpty.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Obrada grešaka
                    }
                });
            }
        });

        otherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("menu");
                menuRef.orderByChild("category").equalTo("other").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Menu> filteredMenuList = new ArrayList<>();

                        for (DataSnapshot menuSnapshot : dataSnapshot.getChildren()) {
                            Menu menu = menuSnapshot.getValue(Menu.class);
                            if (menu != null) {
                                filteredMenuList.add(menu);
                            }
                        }
                        RecyclerView recyclerView = findViewById(R.id.recyclerViewMenu);
                        MenuAdapter menuAdapter = new MenuAdapter(filteredMenuList,menuRef);
                        recyclerView.setAdapter(menuAdapter);

                        menuAdapter.updateData(filteredMenuList);

                        if (filteredMenuList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            textViewMenuEmpty.setVisibility(View.VISIBLE);
                            imageViewMenuEmpty.setVisibility(View.VISIBLE);

                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            textViewMenuEmpty.setVisibility(View.GONE);
                            imageViewMenuEmpty.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Obrada grešaka
                    }
                });
            }
        });

        foodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("menu");
                menuRef.orderByChild("category").equalTo("food").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Menu> filteredMenuList = new ArrayList<>();

                        for (DataSnapshot menuSnapshot : dataSnapshot.getChildren()) {
                            Menu menu = menuSnapshot.getValue(Menu.class);
                            if (menu != null) {
                                filteredMenuList.add(menu);
                            }
                        }
                        RecyclerView recyclerView = findViewById(R.id.recyclerViewMenu);
                        MenuAdapter menuAdapter = new MenuAdapter(filteredMenuList,menuRef);
                        recyclerView.setAdapter(menuAdapter);
                        menuAdapter.updateData(filteredMenuList);

                        menuAdapter.updateData(filteredMenuList);

                        if (filteredMenuList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            textViewMenuEmpty.setVisibility(View.VISIBLE);
                            imageViewMenuEmpty.setVisibility(View.VISIBLE);

                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            textViewMenuEmpty.setVisibility(View.GONE);
                            imageViewMenuEmpty.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }







    public void accountSettings(View view) {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    public void homeView(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void addItemClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_item_modal, null);

        EditText itemName = dialogView.findViewById(R.id.itemName);
        EditText itemPrice = dialogView.findViewById(R.id.itemPrice);
        EditText itemCategory = dialogView.findViewById(R.id.itemCategory);
        Button addImageButton = dialogView.findViewById(R.id.setImage);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        builder.setView(dialogView)
                .setTitle("Add item")
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", (dialog, which) -> {
                });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = itemName.getText().toString();
                        String price = itemPrice.getText().toString();
                        String category = itemCategory.getText().toString();

                        if (name.isEmpty()) {
                            Toast.makeText(view.getContext(), "Name is required", Toast.LENGTH_SHORT).show();
                        } else if (price.isEmpty()) {
                            Toast.makeText(view.getContext(), "Price is required", Toast.LENGTH_SHORT).show();
                        } else if (category.isEmpty()) {
                            Toast.makeText(view.getContext(), "Category is required", Toast.LENGTH_SHORT).show();
                        } else {
                            if (imageUri != null) {
                                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("images");
                                StorageReference imageRef = mStorageRef.child(UUID.randomUUID().toString());

                                try {
                                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                                    UploadTask uploadTask = imageRef.putStream(inputStream);
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageUrl = uri.toString();

                                                    DatabaseReference base = FirebaseDatabase.getInstance().getReference("menu");
                                                    String itemId = base.push().getKey();
                                                    Menu menu = new Menu(itemId, name, price, category, imageUrl);
                                                    base.child(itemId).setValue(menu);




                                                    Toast.makeText(MenuActivity.this, "Successful added", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(view.getContext(), "Image is required", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }

    public void cartView(View view) {
        Intent intent = new Intent(MenuActivity.this, CartActivity.class
        );
        startActivity(intent);
    }

}