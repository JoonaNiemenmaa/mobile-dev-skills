package com.example.emailclient;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(new Runnable() {
            @Override
            public void run() {
                Sender sender = Sender.getInstance();
                try {
                    sender.sendMail(new Email("test1@localhost", new String[]{ "test1@localhost" }, "Android message!", "This message was sent from an Android device!\r\n"));
                } catch (SMTPProtocolException e) {
                    System.err.println(e.getReply());
                }
            }
        });

    }
}