package com.example.trivial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private RouletteView rouletteView;
    private TextView questionText;
    private RadioGroup optionsGroup;
    private Button submitButton, nextButton;
    private TextView scoreText, questionCountText;

    private List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int totalQuestions = 5;

    private static final String PREFS_NAME = "TrivialPrefs";
    private static final String QUESTIONS_COUNT_KEY = "questionsCount";
    private static final String HIGH_SCORE_KEY = "highScore";

    private static final String SERVER_URL = "http://10.0.2.2/trivialandroid/obtener_pregunta.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Cargar preferencias
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        totalQuestions = preferences.getInt(QUESTIONS_COUNT_KEY, 5);

        // Inicializar vistas
        rouletteView = findViewById(R.id.rouletteView);
        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        submitButton = findViewById(R.id.submitButton);
        nextButton = findViewById(R.id.nextButton);
        scoreText = findViewById(R.id.scoreText);
        questionCountText = findViewById(R.id.questionCountText);

        // Configurar botones
        submitButton.setOnClickListener(v -> checkAnswer());
        nextButton.setOnClickListener(v -> showNextQuestion());
        nextButton.setVisibility(View.GONE);

        // Actualizar UI
        updateScoreText();
        updateQuestionCountText();

        // Cargar preguntas
        new FetchQuestionsTask().execute(SERVER_URL + "?num_preguntas=" + totalQuestions);
    }

    private void updateScoreText() {
        scoreText.setText("Puntuación: " + score);
    }

    private void updateQuestionCountText() {
        questionCountText.setText("Pregunta: " + (currentQuestionIndex + 1) + "/" + totalQuestions);
    }

    private void showQuestion(Question question) {
        // Limpiar selecciones previas
        optionsGroup.clearCheck();

        // Actualizar texto de la pregunta
        questionText.setText(question.getQuestion());

        // Preparar opciones (mezclamos para que la correcta no esté siempre en la misma posición)
        List<String> options = new ArrayList<>(question.getOptions());
        Collections.shuffle(options);

        // Actualizar opciones
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) optionsGroup.getChildAt(i);
            if (i < options.size()) {
                rb.setText(options.get(i));
                rb.setVisibility(View.VISIBLE);
            } else {
                rb.setVisibility(View.GONE);
            }
        }

        // Mostrar botón de submit y ocultar next
        submitButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.GONE);
    }

    private void checkAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Selecciona una respuesta", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadio = findViewById(selectedId);
        String selectedAnswer = selectedRadio.getText().toString();
        Question currentQuestion = questions.get(currentQuestionIndex);

        // Deshabilitar opciones
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            optionsGroup.getChildAt(i).setEnabled(false);
        }

        // Comprobar respuesta
        boolean isCorrect = selectedAnswer.equals(currentQuestion.getCorrectAnswer());
        if (isCorrect) {
            score++;
            updateScoreText();
            Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show();
        } else {
            // Mostrar respuesta correcta
            Toast.makeText(this, "Incorrecto. La respuesta correcta es: " +
                    currentQuestion.getCorrectAnswer(), Toast.LENGTH_LONG).show();

            // Marcar la respuesta correcta
            for (int i = 0; i < optionsGroup.getChildCount(); i++) {
                RadioButton rb = (RadioButton) optionsGroup.getChildAt(i);
                if (rb.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
                    rb.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    break;
                }
            }
        }

        // Cambiar botones
        submitButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.VISIBLE);

        // Si es la última pregunta, cambiar texto del botón
        if (currentQuestionIndex == totalQuestions - 1) {
            nextButton.setText("Ver resultados");
        }
    }

    private void showNextQuestion() {
        // Restaurar estado de las opciones
        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            optionsGroup.getChildAt(i).setEnabled(true);
            ((RadioButton) optionsGroup.getChildAt(i)).setTextColor(getResources().getColor(android.R.color.black));
        }

        currentQuestionIndex++;

        // Verificar si hemos terminado todas las preguntas
        if (currentQuestionIndex < totalQuestions) {
            showQuestion(questions.get(currentQuestionIndex));
            updateQuestionCountText();
        } else {
            showResults();
        }
    }

    private void showResults() {
        // Guardar puntuación máxima si corresponde
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int highScore = preferences.getInt(HIGH_SCORE_KEY, 0);

        boolean newHighScore = score > highScore;
        if (newHighScore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(HIGH_SCORE_KEY, score);
            editor.apply();
        }

        // Mostrar diálogo con resultados
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fin del juego");

        String message = "Tu puntuación: " + score + " de " + totalQuestions + "\n\n";
        if (newHighScore) {
            message += "¡Nueva puntuación máxima!";
        } else {
            message += "Puntuación máxima: " + highScore;
        }

        builder.setMessage(message);
        builder.setPositiveButton("Jugar de nuevo", (dialog, which) -> {
            // Reiniciar juego
            currentQuestionIndex = -1;
            score = 0;
            updateScoreText();
            new FetchQuestionsTask().execute(SERVER_URL + "?num_preguntas=" + totalQuestions);
        });

        builder.setNegativeButton("Volver al menú", (dialog, which) -> {
            finish();
        });

        builder.setCancelable(false);
        builder.show();
    }

    private class FetchQuestionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Mostrar mensaje de carga
            questionText.setText("Cargando preguntas...");
            for (int i = 0; i < optionsGroup.getChildCount(); i++) {
                optionsGroup.getChildAt(i).setVisibility(View.GONE);
            }
            submitButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... urls) {
            return NetworkUtils.getResponseFromUrl(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    questions.clear();
                    JSONArray jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject questionJson = jsonArray.getJSONObject(i);
                        String questionText = questionJson.getString("pregunta");
                        String correctAnswer = questionJson.getString("respuesta_correcta");

                        JSONArray optionsArray = questionJson.getJSONArray("opciones");
                        List<String> options = new ArrayList<>();

                        for (int j = 0; j < optionsArray.length(); j++) {
                            options.add(optionsArray.getString(j));
                        }

                        questions.add(new Question(questionText, correctAnswer, options));
                    }

                    currentQuestionIndex = 0;
                    score = 0;
                    updateScoreText();
                    updateQuestionCountText();

                    if (!questions.isEmpty()) {
                        showQuestion(questions.get(0));
                    } else {
                        Toast.makeText(GameActivity.this,
                                "No se pudieron cargar preguntas", Toast.LENGTH_LONG).show();
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GameActivity.this,
                            "Error al procesar las preguntas: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                Toast.makeText(GameActivity.this,
                        "Error al conectar con el servidor", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

}