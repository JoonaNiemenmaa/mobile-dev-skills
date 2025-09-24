package com.example.emailclient;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

            Toast fail_toast = Toast.makeText(this, "Error", Toast.LENGTH_LONG);

            NetworkCallback<String> callback = new NetworkCallback<String>() {
                @Override
                public void onComplete(String result) {
                    startActivity(intent);
                }
            };

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Receiver.startReceiver(mail_address, password);
                    try {
                        Receiver.getInstance().startSession(callback);
                    } catch (IMAPProtocolException exception) {
                        System.err.println(exception.getReply());
                        fail_toast.setText(exception.getReply());
                        fail_toast.show();
                    }
                }
            });

        });
    }

}