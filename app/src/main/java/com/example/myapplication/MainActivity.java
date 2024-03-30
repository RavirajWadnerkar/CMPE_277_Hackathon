package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    EditText editTextQuery;
    TextView textViewResponse;
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextQuery = findViewById(R.id.editTextQuery);
        textViewResponse = findViewById(R.id.textViewResponse);

        Button buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
who is             @Override
            public void onClick(View v) {
                String query = editTextQuery.getText().toString().trim();
                if (!query.isEmpty()) {
                    response(query);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a prompt", Toast.LENGTH_SHORT).show();
                }
            }
        });

        client = new OkHttpClient();
    }

    private void response(String query) {
        String apiKey = "";
        String endpointUrl = "https://api.openai.com/v1/completions";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("model", "gpt-3.5-turbo-instruct");
            requestData.put("prompt", query);
            requestData.put("max_tokens", 30);
            requestData.put("temperature", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(requestData.toString(), JSON);

        Request request = new Request.Builder()
                .url(endpointUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // Convert the response body to a string
                    String resp = responseBody.string();

                    // Parse the JSON response
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        // Extract the text from the JSON response
                        String generatedText = jsonObject.getJSONArray("choices")
                                .getJSONObject(0)
                                .getString("text");

                        // Update UI with the generated text
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                textViewResponse.setText(generatedText);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
