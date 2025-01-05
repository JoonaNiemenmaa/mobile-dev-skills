package com.example.emailclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListMailActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_mail);

        RecyclerView mail_recycler_view = findViewById(R.id.mailRecyclerView);

        mail_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        MailRecyclerViewAdapter mail_recycler_view_adapter = new MailRecyclerViewAdapter(new ArrayList<>());
        mail_recycler_view.setAdapter(mail_recycler_view_adapter);

     //   String user_address = getIntent().getStringExtra("com.example.emailclient.ADDRESS");
     //   String password = getIntent().getStringExtra("com.example.emailclient.PASSWORD");

        ReceiverWrapper receiver_wrapper = new ReceiverWrapper(Receiver.getInstance());

        receiver_wrapper.fetchMail("INBOX", new Callback<ArrayList<Email>>() {
            @Override
            public void onComplete(ArrayList<Email> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mail_recycler_view_adapter.updateData(result);
                    }
                });
            }
        });

    }

}
