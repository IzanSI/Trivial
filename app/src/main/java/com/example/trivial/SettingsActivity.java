package com.example.trivial;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private SeekBar questionsSeekBar;
    private TextView questionsCountText;
    private Button resetScoreButton;
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "TrivialPrefs";
    private static final String QUESTIONS_COUNT_KEY = "questionsCount";
    private static final String HIGH_SCORE_KEY = "highScore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Inicializar preferencias
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Inicializar vistas
        questionsSeekBar = findViewById(R.id.questionsSeekBar);
        questionsCountText = findViewById(R.id.questionsCountText);
        resetScoreButton = findViewById(R.id.resetScoreButton);
        Button btnVolver = findViewById(R.id.btnVolver);

        // Configurar seekbar
        int currentQuestions = preferences.getInt(QUESTIONS_COUNT_KEY, 5);
        questionsSeekBar.setProgress(currentQuestions - 5); // Mínimo 5 preguntas
        updateQuestionsText(currentQuestions);

        questionsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int actualCount = progress + 5; // Mínimo 5 preguntas
                updateQuestionsText(actualCount);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int questionCount = seekBar.getProgress() + 5; // Mínimo 5 preguntas
                saveQuestionCount(questionCount);
            }
        });

        // Configurar botón de reinicio
        resetScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetHighScore();
                Toast.makeText(SettingsActivity.this, "Puntuación máxima reiniciada", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Volver
        btnVolver.setOnClickListener(v -> finish());
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
