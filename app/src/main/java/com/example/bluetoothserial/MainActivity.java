package com.example.bluetoothserial;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // STEP 1: Link Java variables to the XML views by their IDs
        TextView titleText = findViewById(R.id.titleText);
        Button sendButton = findViewById(R.id.sendButton);

        // STEP 2: Change the title text from Java (optional — proves the link works)
        titleText.setText("My First App");

        // STEP 3: Attach a click listener to the button
        sendButton.setOnClickListener(v -> {
            showHelloDialog();
        });
    }

    private void showHelloDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Greeting")
            .setMessage("Hello World")
            .setPositiveButton("OK", null)
            .show();
    }
}