package com.example.relative;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class LoadingActivity extends AppCompatActivity {

    ProgressBar progressBar;
    int progress = 0;
    Handler handler = new Handler();
    Runnable progressRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progressBar);
        ImageView gifView = findViewById(R.id.gifView);

        // Load GIF into ImageView using Glide
        Glide.with(this)
                .asGif()
                .load(R.drawable.jumping_gif) // Make sure you have jumping_gif.gif in drawable
                .into(gifView);

        startLoading();
    }

    private void startLoading() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (progress < 100) {
                    progress++;
                    progressBar.setProgress(progress);
                    handler.postDelayed(this, 40); // Update every 40 milliseconds
                } else {
                    // Loading complete -> Move to GameActivity
                    Intent intent = new Intent(LoadingActivity.this, GameActivity.class);
                    startActivity(intent);
                    finish(); // Close LoadingActivity so user cannot return to it
                }
            }
        };
        handler.post(progressRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(progressRunnable); // Clean up handler
    }
}
