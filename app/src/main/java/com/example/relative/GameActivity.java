package com.example.relative;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class GameActivity extends AppCompatActivity {

    GridLayout gridLayout;
    TextView scoreText, levelText, timeText, highScoreText;
    Button endGameButton;

    int[] allImages = {
            R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4,
            R.drawable.img5, R.drawable.img6, R.drawable.img7, R.drawable.img8
    };

    ArrayList<Integer> currentImages = new ArrayList<>();
    ArrayList<Integer> nextImages = new ArrayList<>();
    int correctAnswer, score = 0, level = 1, timeLeft = 30, highScore = 0;
    Handler handler = new Handler();
    Runnable timerRunnable;

    MediaPlayer correctSound, wrongSound;
    SharedPreferences prefs;
    boolean isGameRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gridLayout = findViewById(R.id.gridLayout);
        scoreText = findViewById(R.id.scoreText);
        levelText = findViewById(R.id.levelText);
        timeText = findViewById(R.id.timeText);
        highScoreText = findViewById(R.id.highScoreText);
        endGameButton = findViewById(R.id.endGameButton);

        correctSound = MediaPlayer.create(this, R.raw.correct);
        wrongSound = MediaPlayer.create(this, R.raw.wrong);

        prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        highScore = prefs.getInt("highScore", 0);

        updateUI();
        startTimer();
        startRound();

        endGameButton.setOnClickListener(v -> showEndGameDialog());
    }

    void startTimer() {
        timeLeft = 30;
        isGameRunning = true;

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (timeLeft > 0) {
                    timeText.setText("Time: " + timeLeft);
                    timeLeft--;
                    handler.postDelayed(this, 1000);
                } else {
                    isGameRunning = false;
                    timeText.setText("Time: 0");
                    showEndGameDialog();
                }
            }
        };
        handler.post(timerRunnable);
    }

    void startRound() {
        if (!isGameRunning) return;

        int imageCount = Math.min(3 + level, allImages.length - 1);
        List<Integer> shuffled = new ArrayList<>();
        for (int img : allImages) shuffled.add(img);
        Collections.shuffle(shuffled);

        currentImages.clear();
        for (int i = 0; i < imageCount; i++) {
            currentImages.add(shuffled.get(i));
        }

        showImages(currentImages);
        handler.postDelayed(this::prepareNextRound, 3000);
    }

    void prepareNextRound() {
        if (!isGameRunning) return;

        nextImages = new ArrayList<>(currentImages);
        int newImage = getRandomUnusedImage(currentImages);
        correctAnswer = newImage;
        nextImages.add(newImage);
        Collections.shuffle(nextImages);
        showClickableImages(nextImages);
    }

    int getRandomUnusedImage(List<Integer> used) {
        List<Integer> unused = new ArrayList<>();
        for (int img : allImages) {
            if (!used.contains(img)) unused.add(img);
        }
        Collections.shuffle(unused);
        return unused.get(0);
    }

    void showImages(List<Integer> images) {
        gridLayout.removeAllViews();
        for (int resId : images) {
            ImageView img = new ImageView(this);
            img.setImageResource(resId);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 250;
            params.height = 250;
            params.setMargins(16, 16, 16, 16);
            params.setGravity(Gravity.CENTER);
            img.setLayoutParams(params);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            gridLayout.addView(img);
        }
    }

    void showClickableImages(List<Integer> images) {
        gridLayout.removeAllViews();
        for (int resId : images) {
            ImageView img = new ImageView(this);
            img.setImageResource(resId);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 250;
            params.height = 250;
            params.setMargins(16, 16, 16, 16);
            params.setGravity(Gravity.CENTER);
            img.setLayoutParams(params);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setOnClickListener(v -> {
                if (isGameRunning) checkAnswer(resId);
            });
            gridLayout.addView(img);
        }
    }

    void checkAnswer(int selectedImage) {
        if (!isGameRunning) return;

        if (selectedImage == correctAnswer) {
            correctSound.start();
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            score++;
            level++;
            if (score > highScore) {
                highScore = score;
                prefs.edit().putInt("highScore", highScore).apply();
            }
        } else {
            wrongSound.start();
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        }

        updateUI();
        startRound();
    }

    void updateUI() {
        score = Math.max(score, 0);
        scoreText.setText("Score: " + score);
        levelText.setText("Level: " + level);
        highScoreText.setText("High Score: " + highScore);
    }

    void showEndGameDialog() {
        isGameRunning = false;
        handler.removeCallbacks(timerRunnable);

        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Your Score: " + score + "\nDo you want to play again?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    score = 0;
                    level = 1;
                    updateUI();
                    startTimer();
                    startRound();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    Intent intent = new Intent(GameActivity.this, ScoreActivity.class);
                    intent.putExtra("score", score);
                    intent.putExtra("highScore", highScore);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (correctSound != null) correctSound.release();
        if (wrongSound != null) wrongSound.release();
        handler.removeCallbacks(timerRunnable);
        super.onDestroy();
    }
}
