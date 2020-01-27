package com.dev.shopper.Buyers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.dev.shopper.R;

public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private TextView txtMessage;
    private EditText txtRegId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        txtRegId = findViewById(R.id.txt_reg_id);
        txtMessage = findViewById(R.id.notification_message);


    }
}
