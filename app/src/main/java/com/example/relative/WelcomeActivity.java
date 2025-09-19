package com.example.relative;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Load animated welcome GIF
        ImageView gifView = findViewById(R.id.welcomeGif);
        Glide.with(this)
                .asGif()
                .load(R.drawable.wave_fox)
                .into(gifView);

        findViewById(R.id.btnStart).setOnClickListener(v ->
                startActivity(new Intent(this, LoadingActivity.class)));

        findViewById(R.id.btnCreateAccount).setOnClickListener(v ->
                startActivity(new Intent(this, CreateAccountActivity.class)));
    }
}