package com.example.listapp;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageView image_view = findViewById(R.id.imageView);

        Resources res = getResources();

        String item_name = getIntent().getStringExtra("ITEM_NAME");

        int image = -1;

        switch (item_name) {
            case "Peach":
                image = R.drawable.peaches;
                break;
            case "Tomato":
                image = R.drawable.tomato;
                break;
            case "Squash":
                image = R.drawable.squash;
                break;
        }

        if (image != -1) {
            image_view.setImageBitmap(BitmapFactory.decodeResource(res, image));
        }

    }
}