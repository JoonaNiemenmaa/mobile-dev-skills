package com.example.listapp;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getApplicationContext().getResources();

        String[] names = res.getStringArray(R.array.Names);
        String[] prices = res.getStringArray(R.array.Prices);
        String[] descriptions = res.getStringArray(R.array.Descriptions);


        RecyclerView items = findViewById(R.id.itemsRecyclerView);
        items.setLayoutManager(new LinearLayoutManager(this));
        items.setAdapter(new ListAdapter(names, prices, descriptions));

    }
}