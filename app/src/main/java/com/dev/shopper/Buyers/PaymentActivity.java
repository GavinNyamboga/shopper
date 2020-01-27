package com.dev.shopper.Buyers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.dev.shopper.R;

public class PaymentActivity extends AppCompatActivity {


    ImageView mpesaImg, PaypalBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        PaypalBtn =  findViewById(R.id.paypal_img_btn);
        mpesaImg = findViewById(R.id.mpesa_img_btn);


        mpesaImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentActivity.this, MpesaActivity.class);
                startActivity(intent);
            }
        });


    }
}
