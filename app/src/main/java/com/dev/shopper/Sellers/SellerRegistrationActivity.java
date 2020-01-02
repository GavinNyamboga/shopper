package com.dev.shopper.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.shopper.Buyers.MainActivity;
import com.dev.shopper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {

    private Button sellerLogin;
    private EditText nameInput, phoneInput, emailInput, passwordInput, addressInput;
    private  Button registerBtn;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        mAuth = FirebaseAuth.getInstance();

        sellerLogin = (Button) findViewById(R.id.seller_have_account_btn);
        nameInput = (EditText) findViewById(R.id.seller_name);
        phoneInput = (EditText) findViewById(R.id.seller_phone);
        emailInput = (EditText) findViewById(R.id.seller_email);
        passwordInput = (EditText) findViewById(R.id.seller_password);
        addressInput = (EditText) findViewById(R.id.seller_address);

        registerBtn = (Button) findViewById(R.id.seller_register_btn);

        loadingBar = new ProgressDialog(this);


        sellerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class);
                startActivity(intent);

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerSeller();
            }
        });

    }

    private void registerSeller()
    {
        final String name =nameInput.getText().toString();
        final String phone = phoneInput.getText().toString();
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();
        final String address = addressInput.getText().toString();

        if (!name.equals("") && !phone.equals("") && !email.equals("") && !password.equals("") && !address.equals(""))
        {
            loadingBar.setTitle("creating Account");
            loadingBar.setMessage("chill kiasi....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful())
                           {
                               final DatabaseReference rootRef;
                               rootRef = FirebaseDatabase.getInstance().getReference();

                               String sid = mAuth.getCurrentUser().getUid();//sid = seller ID

                               HashMap<String, Object> sellerMap = new HashMap<>();
                               sellerMap.put("sid",sid);
                               sellerMap.put("phone",phone);
                               sellerMap.put("email",email);
                               sellerMap.put("address",address);
                               sellerMap.put("name",name);

                               rootRef.child("Sellers").child(sid).updateChildren(sellerMap)
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task)
                                           {
                                               loadingBar.dismiss();
                                               Toast.makeText(SellerRegistrationActivity.this, "you're registered successfully", Toast.LENGTH_SHORT).show();

                                               Intent intent = new Intent(SellerRegistrationActivity.this, SellerHomeActivity.class);
                                               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                               startActivity(intent);
                                               finish();

                                           }
                                       });


                           }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Please input all fields!", Toast.LENGTH_SHORT).show();
        }
    }
}
