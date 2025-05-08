package com.example.trivial;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    // NUEVO: preferencias temporales de música y efectos
    public static boolean musicEnabled = false;
    public static boolean soundEnabled = false;

    // UI existente
    private SeekBar questionsSeekBar;
    private TextView questionsCountText;
    private Button resetScoreButton;
    private Button btnVolver;

    // NUEVO: switches
    private Switch switchMusic;
    private Switch switchSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Inicialización de elementos ya existentes
        questionsSeekBar = findViewById(R.id.questionsSeekBar);
        questionsCountText = findViewById(R.id.questionsCountText);
        resetScoreButton = findViewById(R.id.resetScoreButton);
        btnVolver = findViewById(R.id.btnVolver);

        questionsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = Math.max(progress, 1);
                questionsCountText.setText("Preguntas por ronda: " + value);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        resetScoreButton.setOnClickListener(v -> {
            // Aquí iría tu lógica para reiniciar la puntuación máxima
        });

        btnVolver.setOnClickListener(v -> finish());

        // NUEVO: inicializar switches
        switchMusic = findViewById(R.id.switch_music);
        switchSound = findViewById(R.id.switch_sound_effects);

        // Estados iniciales
        switchMusic.setChecked(musicEnabled);
        switchSound.setChecked(soundEnabled);

        // Listener para música de fondo
        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            musicEnabled = isChecked;
            Intent intent = new Intent(this, MusicService.class);
            if (isChecked) {
                startService(intent);
            } else {
                stopService(intent);
            }
        });

        // Listener para efectos de sonido
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            soundEnabled = isChecked;
        });
    }
}
