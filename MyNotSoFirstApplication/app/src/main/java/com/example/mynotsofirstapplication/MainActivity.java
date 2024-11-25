package com.example.mynotsofirstapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = findViewById(R.id.addButton);

        EditText firstNumEdit = findViewById(R.id.firstNumEdit);
        EditText secondNumEdit = findViewById(R.id.secondNumEdit);

        TextView resultText = findViewById(R.id.resultText);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first_num_string = firstNumEdit.getText().toString();
                String second_num_string = secondNumEdit.getText().toString();

                if (!first_num_string.equals("") && !second_num_string.equals("")) {
                    int num1 = Integer.parseInt(firstNumEdit.getText().toString());
                    int num2 = Integer.parseInt(secondNumEdit.getText().toString());

                    int result = num1 + num2;

                    resultText.setText(Integer.toString(result));
                }

            }
        });
    }
}