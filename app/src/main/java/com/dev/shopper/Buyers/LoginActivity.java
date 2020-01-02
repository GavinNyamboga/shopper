package com.dev.shopper.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.shopper.Admin.AdminCategoryActivity;
import com.dev.shopper.Model.Users;
import com.dev.shopper.Prevalent.Prevalent;
import com.dev.shopper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText loginPhone, loginPassword;
    private Button LoginButton;
    private TextView AdminLink, NotAdminLink, ForgotPassword;
    private ProgressDialog loadingBar;

    private String parentDBName = "Users";
    private CheckBox RememberMeChkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        loginPhone = (EditText) findViewById(R.id.login_phone);
        loginPassword = (EditText) findViewById(R.id.login_password);
        AdminLink = (TextView) findViewById(R.id.admin_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_link);
        ForgotPassword = (TextView) findViewById(R.id.forgot_password);


        loadingBar = new ProgressDialog(this);

        RememberMeChkBox = (CheckBox) findViewById(R.id.remember_me);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                RememberMeChkBox.setVisibility(View.INVISIBLE);
                parentDBName = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDBName = "Users";

            }
        });


    }

    private void loginUser() {
        String phone = loginPhone.getText().toString();
        String password = loginPassword.getText().toString();

        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "please enter your phone number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "please enter your password", Toast.LENGTH_SHORT).show();
        }
        else{

            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("chill kiasi....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            
            AllowAccess(phone,password);
        }
    }


    private void AllowAccess(final String phone, final String password)
    {
        if (RememberMeChkBox.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(parentDBName).child(phone).exists())
                {

                    Users usersData = dataSnapshot.child(parentDBName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            if (parentDBName.equals("Admins"))
                            {

                                Toast.makeText(LoginActivity.this,"logged in successfully as an admin",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }

                            else if (parentDBName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this,"logged in successfully",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUsers = usersData;
                                startActivity(intent);
                            }


                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "password is incorrect",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Account does not exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
