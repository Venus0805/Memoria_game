package com.example.relative;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class ScoreActivity extends AppCompatActivity {

    TextView scoreText, highScoreText;
    Button playAgainButton, exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scoreText = findViewById(R.id.scoreText);
        highScoreText = findViewById(R.id.highScoreText);
        playAgainButton = findViewById(R.id.playAgainButton);
        exitButton = findViewById(R.id.exitButton);
        ImageView scoreGif = findViewById(R.id.scoreGif);

        Glide.with(this)
                .asGif()
                .load("https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExdm10NjYzd2wwdGd5bWt3b2l5Z2U3YzZpNDRtdnR6a3E4ZG83N3cxaCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/3o7TKzR9fHArBz5IHK/giphy.gif")
                .into(scoreGif);

        int score = getIntent().getIntExtra("score", 0);
        int highScore = getIntent().getIntExtra("highScore", 0);

        scoreText.setText("Your Score: " + score);
        highScoreText.setText("High Score: " + highScore);

        playAgainButton.setOnClickListener(v -> {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        });

        exitButton.setOnClickListener(v -> finishAffinity());
    }
}
