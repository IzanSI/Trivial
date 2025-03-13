package com.example.trivial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class HowToPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_to_play_activity);

        // Botón Volver
        Button btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());


        Button btnJugar = findViewById(R.id.btnJugar);
        btnJugar.setOnClickListener(v -> {
            Intent intent = new Intent(HowToPlayActivity.this, GameActivity.class);
            startActivity(intent);
        });
    }
}

