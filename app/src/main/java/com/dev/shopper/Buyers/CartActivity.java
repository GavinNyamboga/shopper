package com.dev.shopper.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dev.shopper.Admin.AdminMaintainProductsActivity;
import com.dev.shopper.Model.Cart;
import com.dev.shopper.Prevalent.Prevalent;
import com.dev.shopper.R;
import com.dev.shopper.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextBtn;
    private TextView txtTotalAmount, txtMsg1;



    private int overTotalPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextBtn = findViewById(R.id.next_btn);
        txtTotalAmount = findViewById(R.id.total_price);
        txtMsg1 = findViewById(R.id.msg1);



        NextBtn.setOnClickListener(view -> {



            Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
            intent.putExtra("Total Price", String.valueOf(overTotalPrice));
            startActivity(intent);
            finish();
        });


    }


    @Override
    protected void onStart() {
        super.onStart();



        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.currentOnlineUsers.getPhone())
                        .child("Products"), Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final CartViewHolder holder, int position, @NonNull final Cart model)
            {


                holder.txtProductQuantity.setText("Quantity = "+model.getQuantity());
                holder.txtProductName.setText("Price = ksh."+model.getPrice() );
                holder.txtProductPrice.setText(model.getPname());

               //Picasso.get().load(model.getImage()).into(holder.productImage);





                int oneTypeProductTPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTypeProductTPrice;



                NumberFormat format = NumberFormat.getInstance();
                format.setMaximumFractionDigits(1);
                txtTotalAmount.setText("Total price = ksh " + format.format(Integer.valueOf(overTotalPrice)));




                //Edit and delete items in cart


                holder.deleteItem.setOnClickListener(view -> {
                    CharSequence options1[] = new CharSequence[]
                            {
                                    "Yes",
                                    "No"
                            };
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setTitle("Are you sure you want to delete this product?");

                    builder.setItems(options1, (dialogInterface, i) -> {
                        if (i == 0) //admin presses yes
                        {

                            cartListRef.child("User View")
                                    .child(Prevalent.currentOnlineUsers.getPhone())
                                    .child("Products")
                                    .child(model.getPid())
                                    .removeValue()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(CartActivity.this, "Item removed successfully", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(CartActivity.this, CartActivity.class);
                                            startActivity(intent);
                                        }

                                    });

                        }
                        else //admin presses no
                        {
                            Intent intent = new Intent(CartActivity.this, CartActivity.class);
                            startActivity(intent);

                        }

                    });
                    builder.show();
                });





               holder.itemView.setOnClickListener(view -> {
                   CharSequence options12[] = new CharSequence[]
                           {
                                   "Edit quantity"
                           };
                   AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                   builder.setTitle("Cart Options:");

                   builder.setItems(options12, (dialogInterface, i) -> {
                       if (i == 0)
                       {
                           Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                           intent.putExtra("pid", model.getPid());
                           startActivity(intent);

                       }

                   });

                   builder.show();
               });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    private void CheckOrderState()
    {
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUsers.getPhone());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String shippingState = dataSnapshot.child("State").getValue().toString();
                    String username = dataSnapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped"))
                    {
                        txtTotalAmount.setText("Dear " + username + "\n order is shipped successfully");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Congratulations, your order has been shipped successfully..");
                        NextBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "you can purchase more products...", Toast.LENGTH_SHORT).show();

                    }
                    else if (shippingState.equals("not shipped"))
                    {
                        txtTotalAmount.setText("Shipping State = Not Shipped");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        NextBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "you can purchase more products...", Toast.LENGTH_SHORT).show();

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }
}
