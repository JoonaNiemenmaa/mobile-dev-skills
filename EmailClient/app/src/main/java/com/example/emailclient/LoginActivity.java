package com.example.emailclient;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    EditText mail_address_edit_text;
    EditText password_edit_text;
    Button auth_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mail_address_edit_text = findViewById(R.id.addressEditText);
        password_edit_text = findViewById(R.id.passwordEditText);
        auth_button = findViewById(R.id.authenticateButton);

        auth_button.setOnClickListener(v -> {
            String mail_address = mail_address_edit_text.getText().toString();
            String password = password_edit_text.getText().toString();
            //mail_address = "test1@localhost";
            //password = "test1@localhost";
            Intent intent = new Intent(getApplicationContext(), ListMailActivity.class);

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Receiver.startReceiver(mail_address, password);
                    Receiver.getInstance().startSession();
                }
            });

           // intent.putExtra("com.example.emailclient.ADDRESS", mail_address);
           // intent.putExtra("com.example.emailclient.PASSWORD", password);
            startActivity(intent);
        });
    }

}