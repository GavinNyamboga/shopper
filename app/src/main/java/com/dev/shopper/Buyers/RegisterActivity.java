package com.dev.shopper.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.shopper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity {

    private Button RegisterButton;
    private EditText InputName, InputPhone, InputPassword;
   // private  EditText RegisterConfirmPassword;
    private ProgressDialog loadingBar;
    private SweetAlertDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        RegisterButton = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_name);
        InputPhone = (EditText) findViewById(R.id.register_phone);
        InputPassword = (EditText) findViewById(R.id.register_password);
        //RegisterConfirmPassword = (EditText) findViewById(R.id.confirm_password);

        pDialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String name = InputName.getText().toString();
        String phone = InputPhone.getText().toString();
        String password = InputPassword.getText().toString();
       // String confirmPassword = RegisterConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "please enter your name!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "please enter your phone number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "please enter your password", Toast.LENGTH_SHORT).show();
        }
        /*else if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "please confirm your password", Toast.LENGTH_SHORT).show();

            if (!RegisterConfirmPassword.equals(InputPassword)) {
                Toast.makeText(this, "Password do not match", Toast.LENGTH_SHORT).show();
            }
        }*/
        else {


            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Creating your account...");
            pDialog.setCancelable(false);
            pDialog.show();

            validatePhone(name,phone,password);
        }
    }

    private void validatePhone(final String name, final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(phone).exists())){

                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("name",name);
                    userdataMap.put("password", password);

                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "your account has been created",Toast.LENGTH_SHORT).show();
                                        pDialog.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        pDialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, "please try again", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                } else
                    {
                        Toast.makeText(RegisterActivity.this,"This " + phone +" already exists", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,"please try again using a different phone number", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
