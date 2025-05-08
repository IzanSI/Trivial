package com.example.trivial;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializar el MediaPlayer con la música de fondo (archivo en /res/raw)
        mediaPlayer = MediaPlayer.create(this, R.raw.m);
        mediaPlayer.setLooping(true);  // repetir en bucle
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // Iniciar la música si no se está reproduciendo ya
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        // Mantener el servicio ejecutándose hasta que se detenga explícitamente (START_STICKY)
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Detener y liberar el MediaPlayer si existe
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // No vamos a permitir binding (no usado en este caso)
        return null;
    }
}