package com.example.trivial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    // Preferencias compartidas
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "TrivialPrefs";
    private static final String QUESTIONS_COUNT_KEY = "questionsCount";
    private static final String HIGH_SCORE_KEY = "highScore";

    // Variables estáticas para sonido
    public static boolean musicEnabled = false;
    public static boolean soundEnabled = false;

    // UI
    private SeekBar questionsSeekBar;
    private TextView questionsCountText;
    private Button resetScoreButton;
    private Button btnVolver;
    private Switch switchMusic;
    private Switch switchSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Preferencias
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Inicializar vistas existentes
        questionsSeekBar = findViewById(R.id.questionsSeekBar);
        questionsCountText = findViewById(R.id.questionsCountText);
        resetScoreButton = findViewById(R.id.resetScoreButton);
        btnVolver = findViewById(R.id.btnVolver);

        // Inicializar nuevos switches
        switchMusic = findViewById(R.id.switch_music);
        switchSound = findViewById(R.id.switch_sound_effects);

        // Configurar estado inicial del SeekBar
        int currentQuestions = preferences.getInt(QUESTIONS_COUNT_KEY, 5);
        questionsSeekBar.setProgress(currentQuestions - 5); // Mínimo 5
        updateQuestionsText(currentQuestions);

        questionsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateQuestionsText(progress + 5);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                saveQuestionCount(seekBar.getProgress() + 5);
            }
        });

        // Reiniciar puntuación
        resetScoreButton.setOnClickListener(v -> {
            resetHighScore();
            Toast.makeText(SettingsActivity.this, "Puntuación máxima reiniciada", Toast.LENGTH_SHORT).show();
        });

        // Botón volver
        btnVolver.setOnClickListener(v -> finish());

        // Estado inicial de los switches
        switchMusic.setChecked(musicEnabled);
        switchSound.setChecked(soundEnabled);

        // Listener de música
        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            musicEnabled = isChecked;
            Intent intent = new Intent(this, MusicService.class);
            if (isChecked) {
                startService(intent);
            } else {
                stopService(intent);
            }
        });

        // Listener de efectos
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            soundEnabled = isChecked;
        });
    }

    private void updateQuestionsText(int count) {
        questionsCountText.setText("Preguntas por ronda: " + count);
    }

    private void saveQuestionCount(int count) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(QUESTIONS_COUNT_KEY, count);
        editor.apply();
        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
    }

    private void resetHighScore() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(HIGH_SCORE_KEY, 0);
        editor.apply();
    }
}