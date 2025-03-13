package com.example.trivial;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchQuestionTask extends AsyncTask<String, Void, String> {
    private final ResponseListener listener;

    public interface ResponseListener {
        void onResponseReceived(String response);
    }

    public FetchQuestionTask(ResponseListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... urls) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (Exception e) {
            return null;
        }
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            listener.onResponseReceived(result);
        }
    }
}

