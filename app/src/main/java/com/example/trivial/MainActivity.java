package com.example.trivial;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private SoundPool soundPool;
    private int soundIdClick;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View mainLayout = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (SettingsActivity.musicEnabled) {
            Intent musicIntent = new Intent(this, MusicService.class);
            startService(musicIntent);
        }

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(1)
                .build();

        soundIdClick = soundPool.load(this, R.raw.button_click, 1);

        Button btnComoJugar = findViewById(R.id.btnComJugar);
        btnComoJugar.setOnClickListener(v -> {
            playClickSound();
            startActivity(new Intent(MainActivity.this, HowToPlayActivity.class));
        });

        Button btnJugar = findViewById(R.id.btnJugar);
        btnJugar.setOnClickListener(v -> {
            playClickSound();
            startActivity(new Intent(MainActivity.this, GameActivity.class));
        });

        Button btnConfiguracio = findViewById(R.id.btnConfiguracio);
        btnConfiguracio.setOnClickListener(v -> {
            playClickSound();
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            // Deslizar derecha → Cómo jugar
                            startActivity(new Intent(MainActivity.this, HowToPlayActivity.class));
                        } else {
                            // Deslizar izquierda → Jugar
                            startActivity(new Intent(MainActivity.this, GameActivity.class));
                        }
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                // Doble tap → Configuración
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
        });

        mainLayout.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void playClickSound() {
        if (SettingsActivity.soundEnabled) {
            soundPool.play(soundIdClick, 1, 1, 0, 0, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        if (isFinishing() && SettingsActivity.musicEnabled) {
            stopService(new Intent(this, MusicService.class));
        }
    }
}
