package com.example.emailclient;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewMailActivity extends AppCompatActivity {

    TextView subject_text, from_text, to_text, text_content_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mail);

        Email mail = (Email) getIntent().getSerializableExtra("com.example.emailclient.MAIL");

        subject_text = findViewById(R.id.subjectViewMailText);
        from_text = findViewById(R.id.fromText);
        to_text = findViewById(R.id.toText);
        text_content_text = findViewById(R.id.textContentText);

        String text = "Subject: " + mail.getSubject();
        subject_text.setText(text);

        text = "From: " + mail.getSender();
        from_text.setText(text);

        text = "To: " + mail.getRecipients();
        to_text.setText(text);

        text_content_text.setText(mail.getTextContent());


    }
}