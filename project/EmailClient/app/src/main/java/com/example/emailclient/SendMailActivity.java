package com.example.emailclient;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SendMailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);

        Sender sender = new Sender();

        EditText subject_edit_text = findViewById(R.id.subjectEditText);
        EditText recipient_edit_text = findViewById(R.id.recipientEditText);
        EditText text_content_edit_text = findViewById(R.id.textContentEditText);

        Button send_mail_button = findViewById(R.id.sendMailButton);

        Toast toast = Toast.makeText(this, "", Toast.LENGTH_LONG);

        send_mail_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = subject_edit_text.getText().toString();
                String recipient = recipient_edit_text.getText().toString();
                String text_content = text_content_edit_text.getText().toString();

                String[] recipients = new String[]{
                        recipient
                };

                SMTPMail mail = new SMTPMail("test1", recipients, subject, text_content);

                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sender.sendMail(mail);
                        } catch (SMTPProtocolException exception) {
                            System.err.println(exception.getReply());
                            toast.setText(exception.getReply());
                            toast.show();
                        }
                        toast.setText("Message " + subject + " sent successfully!");
                        toast.show();
                    }
                });
            }
        });

    }
}